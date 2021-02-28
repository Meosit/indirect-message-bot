package by.mksn.indimebot.user

import com.vladsch.kotlin.jdbc.Row
import com.vladsch.kotlin.jdbc.sqlQuery
import com.vladsch.kotlin.jdbc.usingDefault
import org.slf4j.LoggerFactory

class UserStore private constructor() {

    init {
        usingDefault { session ->
            session.execute(sqlQuery(CREATE_USERS_TABLE_SQL))
        }
        logger.info("Store initialized")
    }

    fun findByHash(hash: String): User? = usingDefault { session ->
        session.first(sqlQuery(SELECT_BY_HASH, hash, hash)) { row -> row.toBotUser() }
    }

    fun findByIdOrUsername(id: Long, username: String? = null): User? = usingDefault { session ->
        val query = if (username == null)
            sqlQuery(SELECT_BY_ID, id)
        else
            sqlQuery(SELECT_BY_ID_OR_USERNAME, id, username)
        session.first(query) { row -> row.toBotUser() }
    }

    fun upsert(newUser: User): Boolean = usingDefault { session ->
        logger.info("Creating or updating user ${newUser.id}")
        val params = mapOf(
            "id" to newUser.id,
            "name" to newUser.name,
            "token_hash" to newUser.tokenHash,
            "username" to newUser.username,
            "passphrase_hash" to newUser.passphraseHash
        )
        session.update(sqlQuery(UPSERT_SQL, params)) == 1
    }

    fun delete(userId: Long): Boolean = usingDefault { session ->
        logger.info("Deleting user $userId")
        session.update(sqlQuery(DELETE_BY_ID, userId)) == 1
    }

    private fun Row.toBotUser() = User(
        long("id"),
        string("name"),
        string("token_hash"),
        stringOrNull("username"),
        stringOrNull("passphrase_hash")
    )

    companion object {

        private val logger = LoggerFactory.getLogger("UserStore")

        fun create(): UserStore {
           return UserStore()
        }

        private const val CREATE_USERS_TABLE_SQL = """
CREATE TABLE IF NOT EXISTS users (
  id              BIGINT NOT NULL,
  name            TEXT NOT NULL,
  token_hash      CHAR(64) NOT NULL
  username        TEXT NULL,
  passphrase_hash CHAR(64) NULL,
  PRIMARY KEY (id),
  UNIQUE(username),
  UNIQUE(token_hash),
  UNIQUE(passphrase_hash)
)"""

        private const val UPSERT_SQL = """
INSERT INTO users (id, name, token_hash, username, passphrase_hash)
VALUES (:id, :name, :token_hash, :username, :passphrase_hash)
ON CONFLICT (id) DO UPDATE SET 
    name = EXCLUDED.name, 
    token_hash = EXCLUDED.token_hash, 
    username = EXCLUDED.username,
    passphrase_hash = EXCLUDED.passphrase_hash
"""

        private const val SELECT_BY_ID = """
SELECT id, name, token_hash, username, passphrase_hash
FROM users 
WHERE id = ?
"""

        private const val SELECT_BY_ID_OR_USERNAME = """
SELECT id, name, token_hash, username, passphrase_hash
FROM users 
WHERE id = ? OR username = ?
"""

        private const val SELECT_BY_HASH = """
SELECT id, name, token_hash, username, passphrase_hash
FROM users 
WHERE NVL(passphrase_hash, token_hash) = ? OR token_hash = ?
"""

        private const val DELETE_BY_ID = """DELETE FROM users WHERE id = ?"""
    }

}
# Indirect Message Bot

Simple telegram bot/service which allows you to indirectly send some text/image/file to your chat with this bot via 
external service without interacting with Telegram itself. Interaction channel is intentionally unidirectional 
(service -> bot) for security and privacy purposes. All non-command messages sent to this bot are ignored. 

The main use-case of this bot comes from the situation when you do not want (or not able) to login into your telegram 
account on some PC while you need to send some text/image/document into it (e.g. share a link, pass some document, send memes).
This eliminates possibility to compromise your account, the only possible drawback is SPAM (but you always can revoke 
the token, or stop the bot). Bot itself do not store any of the content passed through it.

See [@indimebot](https://t.me/indimebot)

## Bot commands

* `/help` - show help message
* `/start` - generate/regenerate a new user token to be used as service password
* `/passphrase <username><passphrase>` - set a passphrase which can be used instead of a token, must start with your username (for uniqueness). Please do not use any of your passwords, it could be something relatively simple
* `/stop` - delete this user info from bot DB, to start using bot again need send `/start` command again.

## Deployment

This bot is deployed on [Heroku](https://www.heroku.com/what). Settings are stored in heroku Postgres DB and hence up 10K users is supported for free.

#### Required environment variables:

* `APP_URL` - URL of the deployed application, for example `https://indimebot.herokuapp.com/`
* `BOT_TOKEN` - [Telegram bot tokens](https://core.telegram.org/bots/api#authorizing-your-bot) which is used for message sending.
* `DATABASE_URL` - Heroku Postgres database url taken corresponding settings at [data.heroku.com](https://data.heroku.com/)

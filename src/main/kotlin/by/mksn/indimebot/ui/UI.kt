package by.mksn.indimebot.ui

import by.mksn.indimebot.output.maxBotOutputLength
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.util.pipeline.*
import kotlinx.html.*


fun HEAD.styles() {
    link(rel = "stylesheet", href = "//cdnjs.cloudflare.com/ajax/libs/skeleton/2.0.4/skeleton.css")
    link(rel = "stylesheet", href = "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css")
    link(rel = "stylesheet", href = "/assets/skeleton-alerts.css")
    link(rel = "stylesheet", href = "/assets/custom-styles.css")
    link(rel = "shortcut icon", href = "/assets/favicon.ico", type = "image/x-icon")
    link(rel = "icon", href = "/assets/favicon.ico", type = "image/x-icon")
}

suspend fun PipelineContext<Unit, ApplicationCall>.rootPageHtml() {
    call.respondHtml {
        head {
            title { +"IndiMeBot" }
            styles()
        }
        body {
            div("body") {
                h2 { +"Indirect Message Bot" }
                p {
                    +"Messages would be sent to "
                    b { a(href = "https://t.me/indimebot", target = "_blank") { +"@indimebot" } }
                }
                p {
                    +"""
                     This service allows you to indirectly send some text/image/file to your chat with this bot without 
                     interacting with Telegram itself. Interaction channel is intentionally unidirectional 
                     (service -> bot) for security and privacy purposes.
                     """.trimIndent()
                }
                form {
                    id = "send-form"
                    div("row") {
                        div("seven columns") {
                            label {
                                htmlFor = "token"
                                +"Your service token or passphrase"
                            }
                        }
                        div("five columns") {
                            label(classes = "u-pull-right") {
                                input(InputType.checkBox) {
                                    id = "save-token"
                                    checked = true
                                }
                                span("label-body") { +"Save token in the local storage after submit" }
                            }
                        }
                        div("twelve columns") {
                            input(InputType.password, classes = "u-full-width") {
                                id = "token"
                                required = true
                            }
                        }
                    }
                    label {
                        htmlFor = "message-text"
                        +"Message to send"
                    }
                    textArea(classes = "u-full-width u-full-height") {
                        maxLength = "$maxBotOutputLength"
                        id = "message-text"
                        placeholder = "Hello from the other side.."
                        required = false
                    }
                    div("row") {
                        div("eight columns") {
                            label("button") {
                                htmlFor = "message-attachment"
                                i("fa fa-file")
                                +" Attachment (max 10MB)"
                            }
                            input(InputType.file) {
                                id = "message-attachment"
                                multiple = false
                                required = false
                            }
                            span("label-body") {
                                id = "attachment-name"
                            }
                        }
                        div("four columns") {
                            label(classes = "u-pull-right") {
                                input(InputType.checkBox) {
                                    id = "compress-images"
                                    checked = true
                                }
                                span("label-body") { +"Send images with compression" }
                            }
                        }
                    }
                    div("display-center") {
                        input(InputType.submit, classes = "button-primary") {
                            value = "Send to the bot"
                        }
                    }
                    p("alert hidden") {
                        id = "result-notification"
                    }
                }
            }
            script {
                type = "text/javascript"
                src = "//code.jquery.com/jquery-3.3.1.min.js"
            }
            script {
                type = "text/javascript"
                src = "/assets/send.js"
            }
        }
    }
}
$(function() {
  $(window).on("load", function() {
    if (typeof(Storage) !== "undefined") {
      var token = localStorage.getItem("indimebot-token");
      if (typeof(token) !== "undefined") {
        $('#token').val(token)
      }
    }
  });
  $('#send-form').on("submit", function(e) {
    e.preventDefault();

    var data = {
      "token": $('#token').val(),
      "save-token": $('#save-token').prop("checked"),
      "message-text": $('#message-text').val()
    }

    if (typeof(Storage) !== "undefined") {
      if (data["save-token"] === true) {
        localStorage.setItem("indimebot-token", data["token"]);
      } else {
        localStorage.removeItem("indimebot-token");
      }
    }

    $.ajax({
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify({
        "token": data["token"],
        "message": data["message-text"]
      }),
      url: '/send-message',
      statusCode: {
          200: function(xhr) {
            $('#result-notification').finish().removeClass("alert-error").addClass("alert-success").removeClass("hidden")
            $('#result-notification').text("Message sent!").fadeOut(10000, function() {
              $('#result-notification').addClass("hidden").removeAttr("style");
            });
            $('#message-text').val("")
          },
          400: function(xhr) {
            $('#result-notification').finish().removeClass("alert-success").addClass("alert-error").removeClass("hidden")
            $('#result-notification').text("Invalid input passed").fadeOut(10000, function() {
              $('#result-notification').addClass("hidden").removeAttr("style");
            });
          },
          404: function(xhr) {
            $('#result-notification').finish().removeClass("alert-success").addClass("alert-error").removeClass("hidden")
            $('#result-notification').text("Provided token or passphrase is invalid").fadeOut(10000, function() {
              $('#result-notification').addClass("hidden").removeAttr("style");
            });
          }
        }
    });
  });
});
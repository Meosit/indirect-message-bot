$(function() {
  $(window).on("load", function() {
    if (typeof(Storage) !== "undefined") {
      var token = localStorage.getItem("indimebot-token");
      if (typeof(token) !== "undefined") {
        $('#token').val(token)
      }
    }
  });

  function isBlank(str) {
      return (!str || /^\s*$/.test(str));
  }

  function showAlert(message, success) {
    if (success) {
      $('#result-notification').finish().removeClass("alert-error").addClass("alert-success").removeClass("hidden")
    } else {
      $('#result-notification').finish().removeClass("alert-success").addClass("alert-error").removeClass("hidden")
    }
    $('#result-notification').text(message).fadeOut(10000, function() {
      $('#result-notification').addClass("hidden").removeAttr("style");
    });
  }

  var messageAttachment = null;

  function validateAttachment() {
    if(messageAttachment != null) {
      if (messageAttachment.size > (10 * 1024 * 1024)) {
        showAlert("Attachment size must be less than 10 Megabytes", false);
        messageAttachment = null;
        $('#attachment-name').text('')
      }
    }
  }

  $('#message-attachment').on('change', function(e) {
    if (e.target.files.length >= 1) {
      messageAttachment = e.target.files[0]
      $('#attachment-name').text(messageAttachment.name)
      validateAttachment()
    } else {
      messageAttachment = null
      $('#attachment-name').text("")
    }
  });

  $(window).on("paste", function(event){
    var items = (event.clipboardData || event.originalEvent.clipboardData).items;
    for (index in items) {
      var item = items[index];
      if (item.kind === 'file') {
        event.preventDefault();
        messageAttachment = item.getAsFile();
        $('#attachment-name').text(messageAttachment.name)
        validateAttachment()
        break;
      }
    }
  })

  $(window).on("drop", function(ev) {
    // Prevent default behavior (Prevent file from being opened)
    ev.preventDefault();
    var dataTransfer = (ev.dataTransfer || ev.originalEvent.dataTransfer);
    if (dataTransfer.items) {
      // Use DataTransferItemList interface to access the file(s)
      for (var i = 0; i < dataTransfer.items.length; i++) {
        // If dropped items aren't files, reject them
        if (dataTransfer.items[i].kind === 'file') {
          var file = dataTransfer.items[i].getAsFile();
          messageAttachment = dataTransfer.files[i]
          $('#attachment-name').text(messageAttachment.name)
          validateAttachment()
          break;
        }
      }
    } else {
      // Use DataTransfer interface to access the file(s)
      for (var i = 0; i < dataTransfer.files.length; i++) {
        messageAttachment = dataTransfer.files[i]
        $('#attachment-name').text(messageAttachment.name)
        validateAttachment()
        break;
      }
    }
  })

  $(window).on("dragover", function(ev) {
    ev.preventDefault();
  })

  $('#send-form').on("submit", function(e) {
    e.preventDefault();

    var messageText = $('#message-text').val();
    var token = $('#token').val();
    if (isBlank(messageText) && messageAttachment == null) {
      showAlert("Please specify text or attach something", false);
      return;
    }

    var formData = new FormData();
    formData.append("token", token);
    formData.append("compress-images", $('#compress-images').prop("checked"));

    if (!isBlank(messageText)) {
      formData.append("message-text", messageText);
    }

    validateAttachment()
    if (messageAttachment != null) {
      formData.append("attachment", messageAttachment, messageAttachment.name)
    }

    if (typeof(Storage) !== "undefined") {
      if ($('#save-token').prop("checked") === true) {
        localStorage.setItem("indimebot-token", token);
      } else {
        localStorage.removeItem("indimebot-token");
      }
    }

    $.ajax({
      type: "POST",
      cache: false,
      processData: false,
      contentType: false,
      data: formData,
      url: '/send-message',
      statusCode: {
          200: function(xhr) {
            showAlert("Message sent!", true)
            $('#message-text').val("")
            $('#message-attachment').val("")
            $('#attachment-name').text("")
          },
          400: function(xhr) {
            showAlert("Invalid input passed", false)
          },
          404: function(xhr) {
            showAlert("Provided token or passphrase is invalid", false)
          }
        }
    });
  });
});
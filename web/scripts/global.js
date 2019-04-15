// import * as $ from "../editor/jquery-min";

$(function () {
    const options = ["undo", "redo", "|", "h1", "h2", "h3", "h4", "h5",
        "h6", "ucwords", "lowercase", "uppercase", "quote", "del", "bold",
        "italic", "hr", "list-ul", "list-ol", "image", "table", "code",
        "code-block", "link", "emoji", "html-entities", "search", "clear",
        "watch", "fullscreen", "preview"];

    const imageFormats = ["jpg", "jpeg", "gif", "png", "bmp", "webp"];

    const editor = editormd("editor", {
        width: "100%",
        height: "92%",
        syncScrolling: "single",
        path: "../editor/lib/",
        theme: "dark",
        emoji: "true",
        previewTheme: "dark",
        editorTheme: "pastel-on-dark",
        saveHTMLToTextarea: true,
        imageUpload: true,
        imageFormats: imageFormats,
        imageUploadURL: "/upload",
        toolbarIcons: function () {
            return options;
        }
    });

    $("#article_data_form").bind("submit", function () {
        $("#markdown_content").text(editor.getMarkdown());
        const add_article = {
            url: "/article?action=add",
            type: "post",
            dataType: "json",
            data: $("#article_data_form").serialize(),
            success: function (data) {
                alert("Success " +JSON.stringify(data));
            }
        };
        jQuery.ajax(add_article);
        return false;
    });

    $("#article_publish").click(function () {
        $("#article_data_form").submit();
    });

    $("#title_image_form").submit(function () {
        const upload_status = {
            url: "/upload?action=upload_status",
            type: "get",
            dataType: "json",
            // async: true,
            success: function (data) {
                // alert(JSON.stringify(data));
                if ('100%' === data.upload_status) {
                    clearInterval(statusInterval);
                    $.ajax(get_url);
                }
                $("#display_upload_status").text(data.upload_status);
            },
            error: function (data) {
                clearInterval(statusInterval);
                alert("file uploaded error" + data);
            }
        };

        const get_url = {
            url: "/upload?action=get_url&delete="
                + $("#title_image_path").val(),
            type: "get",
            // async: true,
            dataType: "json",
            success: function (data) {
                // alert(data.uri);
                $("#title_image_path").val(data.url);
            },
            error: function (data) {
                clearInterval(statusInterval);
                alert("file uploaded error" + data);
            }
        };

        const get_message = {
            url: "/upload?action=get_message",
            type: "get",
            dataType: "json",
            success: function (data) {
                clearInterval(statusInterval);
                $("#display_upload_status").text("0%");
                alert("success => " + JSON.stringify(data))
            },
            error: function (data) {
                clearInterval(statusInterval);
                if (!data) {
                    alert("upload error => " + JSON.stringify(data));
                    $("#display_upload_status").text("0%");
                }
                // alert("error information")
            }
        };
        const statusInterval = setInterval(function () {
            $.ajax(upload_status);
            $.ajax(get_message);
        }, 100);
    });
});
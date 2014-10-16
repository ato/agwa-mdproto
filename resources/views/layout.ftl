[#macro page title]
<!doctype html>
<html>
  <head>
    <meta charset="utf-8"><base href="${request.contextPath()}">
    <title>${title}</title>
    <link rel="stylesheet" href="webjars/bootswatch-spacelab/3.2.0/css/bootstrap.min.css">
  </head>
  <body>
    <div class="content">
     <div class="container">
       [#nested/]
     </div>
    </div>
  </body>
</html>
[/#macro]
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Welcome</title>
</head>
<body>
    <h1>Hello, ${name!"Guest"}!</h1>

     <ul>
           <#list items as item>
                <li>${item}</li>
            </#list>
        </ul>
</body>
</html>
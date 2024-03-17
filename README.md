# Image upload and download
A backend part of an image upload and download application.

In addition to the basic functions, the functions were the following:

- the owner has the opportunity to register as the first administrator, after that, the link is not active;

- users, with the appropriate validations, have the opportunity to register with an activation link and a time limit, and if they do not respond, they are deleted from the database;

- users, if requested, receive a newsletter;

- it is possible to upload images from the user or admin either one image or more locally from the person's computer, verifying that it is empty or not jpg or png format and there is a size limit in case if it is bigger, it is converted by ImageMagick. The images are encrypted by AES algorithm;

- the images are stored in Docker using PostgreSQL;

- the images can also be deleted from the user or admin after decrypted by AES algorithm;

- there is a Swagger documentation.



# JavaPasswordManager
A simple password manager in Java that encrypts your passwords with the AES algorithm.

Instructions:

If running for the first time you will be prompted to insert a number sequence.
This sequence represents the number of parts your key will be split to and their location in the database.
This means the longer the sequence, the safer your data.
IMPORTANT: DO NOT FORGET THE SEQUENCE

After that, you will be presented with any previously stored passwords and their accompanying data and four buttons:

* Copy: copies your password to clipboard. You have to insert the number sequence.
* New: store a new password in the db. You have to insert the number sequence.
* Edit: select a password from the list first and edit aby data you want. You have to insert the number sequence.
* Delete: select a password from the list and delete it.

How it works:

You are given a Secret Key for the AES algorithm.
That key is converted to a byte array and split in as many parts as the you want (Total of numbers in sequence).
1000 more random byte arrays are created to make it harder to find the useful parts.
The key parts are inserted in the list with the dummy data in their expected positions.
All parts are stored in the database.
To insert or retrieve any data, you must insert the sequence to assemble the key.

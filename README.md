# EncryptedSMS

## Problem Definition and Objectives
* The main objective of our project was to derive a practical use out of an implementation of a Cryptosystem.
* And we wanted to target an area where the use of Cryptosystem could bring significant changes.

**Taking into the consideration the above objectives, we decided to implement a “Secure Messaging” application and improve upon the drawbacks of the existing Apps.**


## Need for Secure SMS
* To prevent eavesdropping, the wireless part of a GSM connection is encrypted. However, the first theoretical vulnerabilities in this encryption were already revealed more than 10 years ago. And in October 2009 the researcher Karsten Nohl published a method for hacking into GSM encryption.
* SMS was designed to send non-sensitive data and does not have any security measures.
* In Secure SMS, the message/transaction details remains encrypted as it travels through the telecommunication network and can only be decrypted by the receiver’s mobile.

## We chose RSA.. why?
* Why Public Key Cryptosystem? Symmetric Key Cryptosystem, require the key to be exchanged on a secure channel. Most applications (using AES or DES) require the user to communicate the key over a Voice Call, which is not secure.
* Less Number of Additional SMSs: Use of Diffie Hullman Key Exchange Protocol (to exchange symmetric key) increases the overhead. Since, Alice and Bob should first agree on a Prime Key. And then encryption and decryption follows. Hence, an additional message (as compared to RSA) is exchanged.
* Highly Secure : Breaking RSA is possible only when factorization of very large numbers is possible which is considered to be one of most difficult problem.

## Working of Secure SMS
* Step 1: Sender requests the public key by sending an SMS to the Receiver.
* Step 2: The Receiver generates the public-private key pair and sends the public key to the Sender in an SMS.
* Step 3: Sender encrypts the message with the public key and send the encrypted message via SMS.
* Step 4: Receiver decrypts the encrypted message (using private key) and reads the actual message.

## Conclusion
* The current implementation enables users to send and receive encrypted messages with minimum overhead.
* The size of the public key can be selected by the user. Thus, enabling him/her to control the level of security needed in the message to be sent.
* The user can also periodically change the signature, thus eliminating even the slightest possibility of the system being compromised.

## Screenshots
![Screenshot 1](https://www.evernote.com/l/AZUr7ZpVOUtOXq-D53fKAJhv0dAs1V_1PPwB/image.png "")
![Screenshot 2](https://www.evernote.com/l/AZVg_ZI2OilGCok4BjmG5WwoWHekPySAy8sB/image.png "")

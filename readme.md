# Caller ID API
![caller id](resources/no-caller-id-iphone.jpg)

This short and simple standalone API responds to requests seeking caller-id information. It also adds to local store of information. This API is designed to be lightweight and minimal, which is why it does not make use of an embedded or local datastore nor does it persist information. However, it is a simple extension of this product.

In this API, I use `mount` to manage my application state and dependencies as opposed to component. The choice here was driven by simiplicity; I could choose which components I wanted to mount and I could start and stop them independently of one another if I needed. I use `compojure` to manage the HTTP routing and `ring` for middleware that allows me to parse requests quickly. I make use of the `ring` library where possible for other mundane tasks as well, like constructing standard responses. Finally I use Google's `libphonenumber` which thankfully has a clojure wrapper. This is useful for maintaining a strict format around phone numbers.

## What's In Here

There are two main requests possible (GET, POST). 
1. GET ex. (http:/localhost:3000/query?number=+6461231234)
   - Requires one parameter with a key of `number`.
   - Returns 400 when the parameter number isn't found.
   - Returns 400 if the google api says the number isn't a valid US phone number.
   - Returns 404 if the number wasn't found in our records (but is a valid US phone number).
   - Returns 200 if the number was found, along with all the contexts and names associated with that number.
2. POST ex. (http:/localhost:3000/number?number=+6461231234&context=home&name=pandabear)
   - Requires three parameters with keys of `number`, `context`, `name`.
   - Returns 400 when one of the parameters isn't found.
   - Returns 400 if the google api says the number isn't a valid US phone number.
   - Returns 400 if we already have the same record (based on context and phone number).
   - Returns 201 if we have the phone number but don't have this new context (and adds it to the local store).
     - You can test this addition by performing a GET afterwards.
   - Returns 201 if we didn't have the phone number (and adds it to the local store).

## Logistics
1. Run the app using `lein run`
   a) You may configure the port on which it is run by editing the `config.edn` file that you will find in the `config/dev/` folder at the root level of this directory. This change does not need to be committed to the repository. Rather it can exist locally.
2. Start postman and run the following gets/posts.
   https://www.getpostman.com/collections/923a373c35a77db6ea5a

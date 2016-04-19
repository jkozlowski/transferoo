transferoo.io
=============

[![Circle CI](https://circleci.com/gh/jkozlowski/transferoo.svg?style=svg)](https://circleci.com/gh/jkozlowski/transferoo)

Just a silly little RESTful, in-memory money transfer API.

Things not handled:
* Paging.
* Auth.
* Persistence.
* Performance.
* Sane error handling (returning enumerated error codes).

So pretty much everything you would actually care about...

### Running from Intellij

* class: *io.transferoo.TransferooServer*
* args: *server var/transferoo.yml*

Open [your browser](https://localhost:8443/api/account/d3c02886-2c36-450c-86cf-e199b3ecd9c2).

### Import to IntelliJ

```
$ ./gradlew idea
# Then File -> Open in IntelliJ (do not import from Gradle).
```

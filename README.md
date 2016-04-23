transferoo.io
=============

[![Circle CI](https://circleci.com/gh/jkozlowski/transferoo.svg?style=svg)](https://circleci.com/gh/jkozlowski/transferoo)

Just a silly RESTful, in-memory money transfer API running on Dropwizard.

Features:
* Account creation (accounts can have negative balance from the start).
* Accounts cannot be overdrawn (transaction source must have at least
  the amount in the transaction).
* Transfer amounts must be non-zero.
* Infinite precision (yay for BigDecimals!)

Things not handled:
* Paging.
* Auth.
* Persistence.
* Performance.
* Assume that UUIDs do not clash.

So pretty much everything you would actually care about...

### Running from Intellij

* class: *io.transferoo.TransferooServer*
* args: *server var/transferoo.yml*

Open [your browser](https://localhost:8343/api/accounts/d3c02886-2c36-450c-86cf-e199b3ecd9c2).

### Import to IntelliJ

```
$ ./gradlew idea
# Then File -> Open in IntelliJ (do not import from Gradle).
```

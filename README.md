# gd-io

A Clojure library providing limited access to Google Drive.
The API is minimal, intended primarily for using Google Drive
as a medium for storing backup data.

## Usage
You will need to create OAUTH credentials. 
Look at https://github.com/SparkFund/google-apps-clj/blob/master/README.md for instructions on how to obtain credentials
for your google drive account using the REPL.

You will need to create an EDN file containing the following:

```clojure
{:client-id     "YOUR CLIENT ID"
 :client-secret "YOUR CLIENT SECRET"
 :redirect-uris ["urn:ietf:wg:oauth:2.0:oob" "http://localhost"]
 :auth-map      {:access-token  "YOUR ACCESS TOKEN"
                 :expires-in    3600
                 :refresh-token "YOUR REFRESH TOKEN"
                 :token-type    "Bearer"}}
``` 
## License

Copyright © 2015 Todd Stout

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

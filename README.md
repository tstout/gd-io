# gd-io

A Clojure library providing limited access to Google Drive.
The API is minimal, intended primarily for using Google Drive
as a medium for storing backup data.

## Usage

[![Clojars Project](https://img.shields.io/clojars/v/com.github.tstout/gd-io.svg)]

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

By default, this file is expected to be located at ~/.gd-io/gd-io-creds.clj.
This location can be customized by specifying a map of options to **mk-gdrive**. See 
*(doc mk-gdrive)*

Uploading a file:
```clojure
(ns sample
  (:require [clojure.java.io :refer [file]]
            [gd-io.file :refer [mk-gdrive]]
            [gd-io.protocols :refer [upload]]))

(->
 (mk-gdrive)
 (upload {:title         "backup-1.zip"
          :parent-folder "/backup/my-app"
          :file          (file "path/to/local/file")}))

```
**upload** returns the Drive id of the new file. This id is needed to later delete or download the file.
If any directories in the path specified by **:parent-folder** do not exist, they will be created.
Uploaded files have a drive type of *application/octet-stream*.

Downloading a file:
```clojure
(ns sample
  (:require [gd-io.file :refer [mk-gdrive]]
            [gd-io.protocols :refer [download]]))

(->
 (mk-gdrive)
 (download file-id-from-previous-upload "path/to/local/destintation/file")
```

Deleting a file:
```clojure
(ns sample
  (:require [gd-io.file :refer [mk-gdrive]]
            [gd-io.protocols :refer [rm]]))
(->
  (mk-gdrive)
  (rm file-id-from-previous-upload))
``` 


## License

Copyright © 2015 Todd Stout

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

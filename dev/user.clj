(ns user
  (:require
    [clojure.inspector :refer [inspect inspect-tree inspect-table]]
    [clojure.java.shell :refer [sh]]
    [clojure.data :refer [diff]]
    [gd-io.protocols :refer [ls upload download mkdir rm]]
    [gd-io.internal :refer [mk-drive-service
                            mk-file-meta
                            insert-file
                            trash-file
                            get-files
                            get-folders
                            about
                            about-summary
                            root-folder
                            insert-folder
                            download-file]]
    [gd-io.config :refer [load-default-config mk-config]]
    [clojure.string :as str]
    [gd-io.file :refer [file-by-id
                        file-title
                        normalize-dir-tree
                        dir-titles
                        in?
                        child-dirs
                        file-titles
                        mk-path
                        mk-dir
                        dir-by-title
                        dir-exists?
                        mk-gdrive]]
    [clojure.java.io :refer [as-file
                             resource
                             file
                             make-parents
                             input-stream]]))


(println "--- loading user.clj ---")

(def driveservice (mk-gdrive))
(def test-file (file (resource "test-data.txt")))



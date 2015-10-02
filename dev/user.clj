(ns user
  (:require
    [clojure.java.shell :refer [sh]]
    [gd-io.interop :refer [mk-drive-service
                           mk-file-meta
                           insert-file
                           trash-file]]
    [gd-io.config :refer [load-config]]
    [clojure.string :as str]
    [gd-io.core :refer [ls-dirs
                        dir-tree
                        get-files
                        unique-parents
                        get-folders
                        file-by-id
                        file-title
                        normalize-dir-tree
                        dir-titles
                        get-file-by-id
                        path-vec]]
    [clojure.java.io :refer [as-file
                             file
                             make-parents]]))


(println "--- loading custom repl stuff ---")

(def driveservice (mk-drive-service (load-config) "fin-kratzen"))
(def dirs (ls-dirs driveservice))
(def dtree (dir-tree dirs))
(def ntree (normalize-dir-tree dtree dirs))

(def test-file
  {:drive-service driveservice
   :file-meta     (mk-file-meta {:title         "bkup-test-file2"
                                 :description   "This is a test"
                                 :media-type    "application/octet-stream"
                                 :parent-folder "0ABqBLjW50GqhUk9PVA"})
   :file          (file "target/gd-io-0.1.0-SNAPSHOT-standalone.jar")})
(ns gd-io.file-test
  "Integration test for file operations"
  (:require [gd-io.file :refer [mk-gdrive]]
            [clojure.java.io :refer [file resource delete-file]]
            [gd-io.protocols :refer [ls rm upload download mkdir]]
            [expectations :refer [expect expect-let]]))

(def gdrive (mk-gdrive))
(def tfile (file (resource "test-data.txt")))
(def state (atom {}))
(def home-dir (System/getProperty "user.home"))
(def dest-file (str home-dir "/gd-io-test.txt"))

;;
;; Upload a test file...
;;
(defn before
  {:expectations-options :before-run}
  []
  (swap! state assoc :src-file-id (upload gdrive {:title         "gd-io-test.txt"
                                                  :parent-folder "/gd-io/test"
                                                  :file          tfile})))
;;
;; Remove test files...
;;
(defn after
  {:expectations-options :after-run}
  []
  (rm gdrive (:src-file-id @state))
  (delete-file (:dest-file @state) true))

(expect not-empty (ls gdrive))

;;
;; Verify uploaded file size = downloaded file size
;;
(expect-let [dest-file (:dest-file (swap!
                                     state
                                     assoc
                                     :dest-file
                                     (download gdrive (:src-file-id @state) dest-file)))]
            (.length dest-file)
            (.length tfile))


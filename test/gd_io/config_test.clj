(ns gd-io.config-test
  (:require [expectations :refer [expect side-effects in]]
            [clojure.java.io :refer [make-parents file delete-file]]
            [gd-io.config :refer [mk-config
                                  load-config
                                  stub-config]]))

(defn after
  {:expectations-options :after-run}
  []
  (delete-file
    (format "%s/.gd-io/test.clj" (System/getProperty "user.home"))
    true))

;;
;; Verify loading the configuration creates a convenient stub config file.
;;
(expect stub-config
        (load-config {:fname "test.clj"}))
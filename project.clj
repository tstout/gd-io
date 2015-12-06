(defproject gd-io "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [clj-http "2.0.0"]
                 [com.google.oauth-client/google-oauth-client "1.20.0"]
                 [com.google.api-client/google-api-client-java6 "1.20.0"]
                 [com.google.http-client/google-http-client "1.20.0"]
                 [com.google.api.client/google-api-client-json "1.2.3-alpha"]
                 [com.google.http-client/google-http-client-jackson2 "1.20.0"]
                 [com.google.apis/google-api-services-drive "v2-rev168-1.20.0"]
                 [com.google.gdata/core "1.47.1"]
                 [org.clojure/data.json "0.2.6"]]
  :plugins [[lein-autoexpect "1.7.0"]
            [lein-expectations "0.0.7"]]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [expectations "2.0.9"]]}}
  :repl-options {:init-ns user})

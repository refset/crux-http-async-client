(ns crux-http-async-client.core-test
  #?(:cljs (:require [cljs.test :as t]
                     [promesa.core :as p :refer-macros [alet]]
              ;       [promesa.async-cljs :refer-macros [async]]
                     [crux-http-async-client.core :as api]
                     ))
  #?(:clj  (:require [clojure.test :refer :all]
                     [promesa.core :as p :refer-macros [alet]]
              ;       [promesa.async :refer [async]]
                     [crux-http-async-client.core :as api]
                     ))
  #?(:clj (:import
            [crux.api Crux ICruxAPI ICruxDatasource])))

; TODO only tested with `lein cljs-test` so far, so clj is almost certainly broken

; derived from https://github.com/juxt/crux/blob/6d2fecc85fac686615a78c8d7ee599a8781c6b36/crux-test/test/crux/api_test.clj (crux-wide integration tests)
; TODO ^ could consolidate (using macros?)

; https://purelyfunctional.tv/mini-guide/how-can-you-test-clojurescript-applications-and-libraries/

; for clj development https://github.com/weavejester/hashp

; TODO could start-up for testing using this (currently using crux-docker manually) https://github.com/juxt/crux/blob/bd54020fdda13e4578778381cded269ed2652642/crux-test/test/crux/fixtures/http_server.clj

(def Exception #?(:clj Exception :cljs js/Error))

(def api* (api/new-api-client "http://localhost:3000"))

(t/deftest test-content-hash-invalid
  (t/async
   done
   (alet [content-ivan {:crux.db/id :ivan :name "Ivan"}
          content-hash "b15f8b81a160b4eebe5c84e9e3b65c87b9b2f18e" ; computed by (str (c/new-id content-ivan))
          _ (p/catch
                (api/submitTx api* [[:crux.tx/put content-hash]])
                (fn [error]
                  (t/is (thrown-with-msg? Exception (re-pattern (str content-hash "|HTTP status 400"))
                                          (throw (Exception. (str error)))))
                  (done)))])))

(t/deftest test-can-write-entity-using-map-as-id
  (t/async
   done
   (alet [doc {:crux.db/id {:user "Xwop1A7Xog4nD6AfhZaPgg"} :name "Adam"}
          submitted-tx (p/await (api/submitTx api* [[:crux.tx/put doc]]))
           _ (p/await (api/sync api* (:crux.tx/tx-time submitted-tx)))
          pe (p/await (api/entity (api/db api*) {:user "Xwop1A7Xog4nD6AfhZaPgg"}))] ; TODO should be get not post?
         (t/is (= pe doc))
         (done))))

(comment t/deftest test-can-use-crux-ids
  (t/async
   done
   (alet [id ; #crux/id :https://adam.com ; TODO implement CLJC readers
          doc {:crux.db/id id, :name "Adam"}
          submitted-tx (p/await (api/submitTx api* [[:crux.tx/put doc]]))
          _ (p/await (api/sync api* (:crux.tx/tx-time submitted-tx)))]
         (t/is (api/entity (api/db api*) id))
         (done))))

(t/deftest test-single-id
  (t/async
   done
   (let [valid-time nil;(Date.)
         content-ivan {:crux.db/id :ivan :name "Ivan"}]

     (t/testing "put works with no id"
       (alet [{:crux.tx/keys [tx-time] :as tx} (api/submitTx api* [[:crux.tx/put content-ivan valid-time]])
              _ (p/await (api/await-tx api* tx nil))]
             (t/is
              (api/db api* valid-time tx-time))))

     (t/testing "Delete works with id"
       (alet [tx (p/await (api/submitTx api* [[:crux.tx/delete :ivan]]))]
             (t/is tx)
             (done)))))) ; TODO done works here because it happens to execute after the previous testing, can't rely on this

(ns crux-http-async-client.http-functions
  (:require [promesa.core :as p :include-macros true]
            [clojure.edn :as edn]
            [httpurr.client.node :as http]))

(defn parse-json-body
  [{:keys [body]}]
  (js/JSON.parse body))

(defn clj-body
  [response]
  (js->clj (parse-json-body response)))

(defmulti fetch type)

(defmethod fetch :default [{:keys [method url] :as opts}]
  (if method (assert (#{:post :get} method) (str "Unsupported HTTP method: " (:method opts))))
  (let [opts (-> opts
                 ;(update :method (fnil name :get))
                 (assoc ;:referrerPolicy "origin"
                        :url url))
        _ (prn opts)]
    (let [fp (http/send! opts);(js/fetch url (clj->js opts))
             _ (p/catch fp (fn [error]
                             (.log js/console "harrr2")
                             (.log js/console error)))
             ]
      (p/then fp (fn [resp](p/resolved{:body (edn/read-string (.toString (:body resp)))
       :status (:status resp);(.-status resp)
       :headers {:content-type (get-in resp [:headers "Content-Type"])}
                            }))))))

(defmethod fetch js/String [url]
  (fetch {:url url :method :get}))

(defn fetch-edn [prms]
  (p/map #(update % :body edn/read-string) (fetch prms)))




;(def url "http://localhost:3000")

#_(p/alet [f0 (fetch "http://localhost:3000")
         f00 (p/await f0)
         fp (http/get url)
         fpp (p/await fp)]
        (prn f00)
        (prn fpp))




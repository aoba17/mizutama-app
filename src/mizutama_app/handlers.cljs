(ns mizutama-app.handlers
  (:require
    [re-frame.core :refer [reg-event-db ->interceptor]]
    [clojure.spec.alpha :as s]
    [mizutama-app.db :as db :refer [app-db]]))

;; -- Interceptors ----------------------------------------------------------
;;
;; See https://github.com/Day8/re-frame/blob/develop/docs/Interceptors.md
;;
(defn check-and-throw
  "Throw an exception if db doesn't have a valid spec."
  [spec db]
  (when-not (s/valid? spec db)
    (let [explain-data (s/explain-data spec db)]
      (throw (ex-info (str "Spec check failed: " explain-data) explain-data)))))

(def validate-spec
  (if goog.DEBUG
    (->interceptor
        :id :validate-spec
        :after (fn [context]
                 (let [db (-> context :effects :db)]
                   (check-and-throw ::db/app-db db)
                   context)))
    ->interceptor))

;; -- Handlers --------------------------------------------------------------

(reg-event-db
  :initialize-db
  [validate-spec]
  (fn [_ _]
    app-db))

(reg-event-db
  :set-greeting
  [validate-spec]
  (fn [db [_ value]]
    (assoc db :greeting value)))

(reg-event-db
  :set-camera-permission
  (fn [db [_ status]]
    (prn (str "dispatch arg " status))
    (assoc db :camera-permission status)))

(reg-event-db
  :set-image
  (fn [db [_ {:keys [uri width height base64]}]]
    (prn (str "data:image/jpg;base64," base64))
    (-> db
        (assoc :uri uri)
        (assoc :width width)
        (assoc :height height)
        (assoc :base64 (str "data:image/jpg;base64," base64)))))

(reg-event-db
  :set-camera-ref
  (fn [db [_ ref]]
    (assoc db :camera-ref ref)))

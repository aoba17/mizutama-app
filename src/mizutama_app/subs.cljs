(ns mizutama-app.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :get-greeting
  (fn [db _]
    (:greeting db)))

(reg-sub
  :get-camera-permission
  (fn [db]
    (prn db)
    (:camera-permission db)))

(reg-sub
  :get-image
  (fn [db]
    (:image db)))

(reg-sub
  :get-camera-ref
  (fn [db]
    (:camera-ref db)))

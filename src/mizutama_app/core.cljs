(ns mizutama-app.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [oops.core :refer [ocall]]
            [mizutama-app.handlers]
            [mizutama-app.subs]
            [promesa.core :as p]))

(def ReactNative (js/require "react-native"))
(def expo (js/require "expo"))
(def AtExpo (js/require "@expo/vector-icons"))
(def ionicons (r/adapt-react-class (.-Ionicons AtExpo)))

(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))
(def Alert (.-Alert ReactNative))

(def permissions (js/require "expo-permissions"))
(def expo-camera (.-Camera (js/require "expo-camera")))
(def camera (r/adapt-react-class expo-camera))
(def back-camera (-> expo-camera .-Constants (js->clj :keywordize-keys true) :Type :back))
(def front-camera (-> expo-camera .-Constants (js->clj :keywordize-keys true) :Type :front))
(def touchable-opacity (r/adapt-react-class (.-TouchableOpacity ReactNative)))
(def admob-banner (r/adapt-react-class (.-AdMobBanner (js/require "expo-ads-admob"))))
(def safe-area-view (r/adapt-react-class (.-SafeAreaView ReactNative)))
(def ant-design (r/adapt-react-class (.-AntDesign AtExpo)))

(defn alert [title]
  (.alert Alert title))

(def styles {:controller-area   {:flex            1
                                 :backgroundColor "transparent"
                                 :flexDirection   "row"}
             :controller-button {:flex       1
                                 :alignSelf  "flex-end"
                                 :alignItems "center"}})

(defn image-picker-button []
  [touchable-opacity {:style (:controller-button styles)}
   [ionicons {:name  "ios-photos"
              :size  50
              :color "white"}]])

(defn reverse-camera-button []
  [touchable-opacity {:style (:controller-button styles)}
   [ionicons {:name  "ios-reverse-camera"
              :size  50
              :color "white"}]])

(defn take-picture-button []
  [touchable-opacity {:style (:controller-button styles)}
   [ionicons {:name     "ios-radio-button-on"
              :size     80
              :color    "white"
              :on-press #(.takePictureAsync
                           @(subscribe [:get-camera-ref])
                           (clj->js {:base64 true
                                     :onPictureSaved
                                     (fn [image]
                                       (dispatch [:set-image (js->clj image :keywordize-keys true)]))}))}]])

(defn qa-button []
  [touchable-opacity {:style (:controller-button styles)}
   [ant-design {:name  "question"
                :size  50
                :color "white"}]])

(defn setting-button []
  [touchable-opacity {:style (:controller-button styles)}
   [ionicons {:name  "ios-settings"
              :size  50
              :color "white"}]])

(defn camera-screen []
  [safe-area-view {:style {:flex            1
                           :backgroundColor :lightsteelblue}}
   [camera {:ref   #(dispatch [:set-camera-ref %])
            :style {:flex 1}
            :type  back-camera}
    [view {:style (:controller-area styles)}
     [image-picker-button]
     [reverse-camera-button]
     [take-picture-button]
     [setting-button]
     [qa-button]]]
   [admob-banner {:adUnitID     "ca-app-pub-8254198164854212/5416477435"
                  :testDeviceID "EMULATOR"
                  :bannerSize   "smartBannerPortrait"}]])

(defn permission-denied []
  [view [text "カメラへのアクセスがありません。本体設定にて本アプリのカメラアクセスを許可してください。"]])

(def ask-camera-permission
  (-> (.askAsync permissions (.-CAMERA permissions))
      (p/then (fn [result]
                (let [status (-> result
                                 (js->clj :keywordize-keys true)
                                 :status)]
                  (dispatch [:set-camera-permission status]))))))

(defn app-root []
  (fn []
    (let [camera-permission (subscribe [:get-camera-permission])]
      (cond (nil? @camera-permission)        (fn [] [view])
            (= @camera-permission "denied")  (fn [] [permission-denied])
            (= @camera-permission "granted") (fn [] [camera-screen])))))

(defn init []
  (dispatch [:initialize-db])
  (ocall expo "registerRootComponent" (r/reactify-component app-root)))

(ns damel.browser
  (:require [damel.game-dimensions :as game-dimensions]))

(enable-console-print!)

(defn preload [game]
  (println "preload")
  (let [loader (.-load game)]
    (set! (.-baseURL loader) "http://localhost:9000/resources/img/")
    (set! (.-crossOrigin loader) "anonymous")
    (doto loader
      (.image "elevator" "elevator2.png")
      (.image "elevator" "elevator_cabin.png"))))

(defn- line [])
(defn make-level [game i]
  (let [{:keys [floor elevator level-text]} (game-dimensions/level i)]

    (let [{:keys [y]} elevator
          image (-> (.. game -add)
                    (.image 0 0 "elevator")
                    (.setOrigin 0 0))]
      (set! (.-y image) y))

    (let [{:keys [x y height width]} floor]
      (prn floor)
      (-> (.. game -add)
          (.graphics)
          (.fillStyle 0xFF00FF)
          (.fillRect x y width height)))

    (let [{:keys [x y height]} level-text]
      (-> (.. game -add)
          (.text x y (str "Level " i)
                 #js {:fontSize (str height "px") :fill "#0F0"})))))

(defn create [game]
  (println "create" (js/Object.keys game))
  (let [_            (dotimes [level-number game-dimensions/nb-levels]
                       (make-level game level-number))

        main-camera_ (let [{:keys [bounds zoom]} game-dimensions/main-camera]
                       (->
                         (.. game -cameras -main)
                         (.setBounds (:x bounds) (:y bounds) (:width bounds) (:height bounds))
                         (.setZoom zoom)
                         (.setName "main")))
        mini-camera_ (let [{:keys [position bounds zoom]} game-dimensions/mini-camera]
                       (->
                         (.. game -cameras)
                         (.add (:x position) (:y position) (:width position) (:height position))
                         (.setBackgroundColor 0xFF0000)
                         (.setBounds (:x bounds) (:y bounds) (:width bounds) (:heigh bounds))
                         (.setZoom zoom)
                         (.setName "mini")
                         (.setScroll 0 0)))]

    #_(position elevator_)))

(def i (atom 0))
(defn update-game [game]
  (doto
    (.. game -cameras -main)
    (.setScroll 0 (swap! i inc))))

(def config
  (clj->js
    {:type    js/Phaser.AUTO
     :width   game-dimensions/screen-width
     :height  game-dimensions/screen-height
     :physics {:default "arcade"
               :arcade  {:gravity {:y 200}}}
     :scene   {:preload #(this-as game (preload game))
               :create  #(this-as game (create game))
               :update  #(this-as game (update-game game))}}))

(defonce game (js/Phaser.Game. config))
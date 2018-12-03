(ns damel.browser)

(enable-console-print!)

(def screen-height 600)
(def screen-width 800)
;(def floor-height 10)
;(def nb-levels 10)
;(def level-height 200)
;(def world-height (* nb-levels level-height))
;(def level-text-size 14)

(defn preload [game]
  (println "preload")
  (let [loader (.-load game)]
    (set! (.-baseURL loader) "http://localhost:9000/resources/img/")
    (set! (.-crossOrigin loader) "anonymous")
    (doto loader
      (.image "elevator" "elevator2.png")
      (.image "elevator" "elevator_cabin.png"))))

(defn make-level [game i]
  (let [image           (-> (.. game -add)
                            (.image 0 0 "elevator")
                            (.setOrigin 0 0))
        rectangle       (.getBounds image)
        elevator-height (.-height rectangle)
        floor-y         (- world-height (* i level-height))]
    (set! (.-y image)
          (- floor-y floor-height elevator-height))

    (-> (.. game -add)
        (.graphics)
        (.lineStyle floor-height 0xFF00FF 1.0)
        (.beginPath)
        (.moveTo 0 (- floor-y floor-height))
        (.lineTo 600 (- floor-y floor-height))
        (.closePath)
        (.strokePath))

    (-> (.. game -add)
        (.text 610 (- floor-y level-text-size) (str "Level " i " " floor-y) #js {:fontSize (str level-text-size "px") :fill "#0F0"}))))

(defn create [game]
  (println "create" (js/Object.keys game))
  (let [elevator_    (->
                       (.. game -add)
                       (.image game-obj-factory 0 0 "elevator")
                       (.setOrigin 0 0))


        _            (dotimes [level-number nb-levels]
                       (make-level game level-number))
        main-camera_ (doto
                       (.. game -cameras -main)
                       (.setBounds 0, 0, 700, world-height)
                       (.setZoom 1)
                       (.setName "main"))
        mini-camera_ (->
                       (.. game -cameras)
                       (.add 700 0 100 600)
                       (.setBackgroundColor 0xFF0000)
                       (.setBounds 0, 0, 700, (* 600 3))
                       (.setZoom (/ 1 6.0))
                       (.setName "mini")
                       (.setScroll 0 0))]

    #_(position elevator_)))

(def i (atom 0))
(defn update-game [game]
  (doto
    (.. game -cameras -main)
    (.setScroll 0 (swap! i inc))))

(def config
  (clj->js
    {:type    js/Phaser.AUTO
     :width   screen-width
     :height  screen-height
     :physics {:default "arcade"
               :arcade  {:gravity {:y 200}}}
     :scene   {:preload #(this-as game (preload game))
               :create  #(this-as game (create game))
               :update  #(this-as game (update-game game))}}))

(defonce game (js/Phaser.Game. config))
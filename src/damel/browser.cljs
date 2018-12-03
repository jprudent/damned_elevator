(ns damel.browser)

(enable-console-print!)

(def screen-height 600)
(def screen-width 800)
(def floor-height 50)
(def nb-levels 10)
(def level-height 400)
(def world-height (* nb-levels level-height))

(defn position [image]
  (let [rectangle       (.getBounds image)
        elevator-height (.-height rectangle)]
    (set! (.-y image) (- screen-height floor-height elevator-height))))

(defn preload [game]
  (println "preload")
  (let [loader (.-load game)]
    (set! (.-baseURL loader) "http://localhost:9000/resources/img/")
    (set! (.-crossOrigin loader) "anonymous")
    (doto loader
      (.image "elevator" "elevator.png"))))

(defn make-level [game i]
  (let [image           (-> (.. game -add)
                            (.image 0 0 "elevator")
                            (.setOrigin 0 0))
        rectangle       (.getBounds image)
        elevator-height (.-height rectangle)
        floor-y         (* i level-height)]
    (set! (.-y image)
          (- world-height floor-y floor-height elevator-height))

    (-> (.. game -add)
        (.graphics)
        (.lineStyle 5 0xFF00FF 1.0)
        (.beginPath)
        (.moveTo 0 floor-y)
        (.lineTo 700 floor-y)
        (.closePath)
        (.strokePath))

    (-> (.. game -add)
        (.text 0 floor-y (str "Level " i) #js {:fontSize "32px" :fill "#0F0"}))))

(defn create [game]
  (println "create" (js/Object.keys game))
  (let [game-obj-factory (.-add game)
        ;elevator_        (doto
        ;                   (.image game-obj-factory 0 0 "elevator")
        ;                   (.setOrigin 0 0))
        ;
        ;elevator2_       (doto
        ;                   (.image game-obj-factory 0 500 "elevator")
        ;                   (.setOrigin 0 0))

        _                (dotimes [level-number nb-levels]
                           (make-level game level-number))
        main-camera_     (doto
                           (.. game -cameras -main)
                           (.setBounds 0, 0, 700, world-height)
                           (.setZoom 0.4)
                           (.setName "main"))
        mini-camera_     (->
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
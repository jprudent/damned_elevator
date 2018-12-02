(ns damel.phaser-demo)

(enable-console-print!)

(defn preload [game]
  (println "preload")
  (let [loader (.-load game)]
    (set! (.-baseURL loader) "http://labs.phaser.io/")
    (set! (.-crossOrigin loader) "anonymous")
    (doto loader
      (.image "sky" "assets/skies/space3.png")
      (.image "logo" "assets/sprites/phaser3-logo.png")
      (.image "red" "assets/particles/red.png"))))

(defn create [game]
  (println "create" (js/Object.keys game))
  (let [game-obj-factory (.-add game)
        sky_             (.image game-obj-factory 400 300 "sky")
        particles        (.particles game-obj-factory "red")
        emitter          (.createEmitter particles
                                         #js {:speed     10
                                              :scale     #js {:start 1 :end 0}
                                              :blendMode "ADD"})
        phy-obj-factory  (-> game .-physics .-add)
        logo             (doto
                           (.image phy-obj-factory 400 100 "logo")
                           (.setVelocity 50 10)
                           (.setBounce 1 1)
                           (.setCollideWorldBounds true))]
    (.startFollow emitter logo)))

(def config
  #js {:type    js/Phaser.AUTO
       :width   800
       :height  600
       :physics #js {:default "arcade"
                     :arcade  #js {:gravity #js {:y 200}}}
       :scene   #js {:preload #(this-as game (preload game))
                     :create  #(this-as game (create game))}})

(def game (js/Phaser.Game. config))

(println config)


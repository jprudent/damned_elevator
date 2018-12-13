(ns damel.game-objects-factory
  (:require [damel.game-dimensions :as game-dimensions]
            [damel.state :as state]
            [damel.phaser-utils :as phutils]))

(defn preload-assets
  "Preload all assets for the game"
  [game]
  (println "preload")
  (let [loader (.-load game)]
    (set! (.-baseURL loader) "http://localhost:9000/resources/img/")
    (set! (.-crossOrigin loader) "anonymous")
    (doto loader
      (.image "elevator" "elevator.png")
      (.image "cabin" "elevator_cabin.png")
      (.image "female0" "female0.png"))))

(defn make-level
  "Make level i"
  [game i]
  (let [{:keys [floor elevator level-text]} (game-dimensions/level i)]

    (let [{:keys [x y]} elevator]
      (-> (.. game -add)
          (.image 0 0 "elevator")
          (.setOrigin 0 0)
          (.setPosition x y)))

    (let [{:keys [x y height width]} floor]
      (-> (.. game -add)
          (.graphics)
          (.fillStyle 0xFF00FF)
          (.fillRect x y width height)))

    (let [{:keys [x y height]} level-text]
      (phutils/text-at game x y height (str "Level " i)))))

(defn make-cabin
  "Make the elevator cabin"
  [game]
  (let [{:keys [x y]} game-dimensions/cabin
        cabin (-> (.. game -physics -add)
                  (.image 0 0 "cabin")
                  (.setName "cabin")
                  (.setOrigin 0 0)
                  (.setPosition x y))]
    (-> (.. cabin -body)
        (.setMass 1000)
        (.setVelocity 0 0))))

(defn make-main-camera
  [game]
  (let [{:keys [bounds zoom]} game-dimensions/main-camera]
    (->
      (.. game -cameras -main)
      (.setBounds (:x bounds) (:y bounds) (:width bounds) (:height bounds))
      (.setZoom zoom)
      (.setName "main"))))

(defn make-mini-camera
  "Make the building camera on the right side"
  [game]
  (let [{:keys [position bounds zoom]} game-dimensions/mini-camera]
    (->
      (.. game -cameras)
      (.add (:x position) (:y position) (:width position) (:height position))
      (.setBackgroundColor 0x222222)
      (.setBounds (:x bounds) (:y bounds) (:width bounds) (:heigh bounds))
      (.setZoom zoom)
      (.setName "mini")
      (.setScroll 0 0))))

(defn create-building
  "Initialisation of the game"
  [state game]

  (do
    (dotimes [level-number game-dimensions/nb-levels]
      (make-level game level-number))
    (make-main-camera game)
    (make-mini-camera game))

  ;; the cabin is the only object we want to keep track of
  (state/set-cabin-sprite state (make-cabin game)))

(defn make-worker
  "Make a worker"
  [game {:keys [name current-level id] :as worker_}]
  (let [{:keys [x y scale]} (game-dimensions/worker-at-level current-level)]
    (-> (.. game -add)
        (.image 0 0 "female0")
        (.setName name)
        (.setScale scale)
        (.setOrigin 0 0)
        (.setPosition x y))))

(defn make-walking-worker-tween
  [game worker-sprite destination-x]
  (-> (.. game -tweens)
      (.add (clj->js {:targets worker-sprite
                      :x       {:value    destination-x
                                :duration (* 30000 (rand))}
                      :angle   {:value    5
                                :yoyo     true
                                :duration 100
                                :repeat   -1}}))))
(ns damel.browser
  (:require [damel.game-dimensions :as game-dimensions]
            [damel.elevator-cabin :as elevator-cabin]
            [damel.workers :as workers]))

(enable-console-print!)

(defn preload [game]
  (println "preload")
  (let [loader (.-load game)]
    (set! (.-baseURL loader) "http://localhost:9000/resources/img/")
    (set! (.-crossOrigin loader) "anonymous")
    (doto loader
      (.image "elevator" "elevator2.png")
      (.image "cabin" "elevator_cabin.png")
      (.image "female0" "female0.png"))))

(defn- text-at
  [game x y height text]
  (-> (.. game -add)
      (.text x y text
             #js {:fontSize (str height "px") :fill "#0F0"})))

(defn make-level [game i]
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
      (text-at game x y height (str "Level " i)))))

(def state (atom {:ticks              0
                  :cabin              elevator-cabin/cabin-0
                  :objects            {:cabin   nil
                                       :workers {}}
                  :cabin-current-move nil
                  :workers            workers/workers-0}))

(defn emit-cabin-command!
  [command]
  (swap! state update :cabin #(elevator-cabin/add-command % command)))

(defn emit-worker-command!
  [command]
  (swap! state update :workers #(workers/add-command % command)))

(defn init-cabin [game]
  (let [{:keys [x y]} game-dimensions/cabin
        {:keys [mass]} elevator-cabin/cabin-0
        cabin (-> (.. game -physics -add)
                  (.image 0 0 "cabin")
                  (.setName "cabin")
                  (.setOrigin 0 0)
                  (.setPosition x y))]
    (-> (.. cabin -body)
        (.setMass mass)
        (.setVelocity 0 0))
    (swap! state assoc-in [:objects :cabin] cabin)))

(defn create [game]
  (println "create" (js/Object.keys game))

  (dotimes [level-number game-dimensions/nb-levels]
    (make-level game level-number))

  (init-cabin game)

  (let [{:keys [bounds zoom]} game-dimensions/main-camera]
    (->
      (.. game -cameras -main)
      (.setBounds (:x bounds) (:y bounds) (:width bounds) (:height bounds))
      (.setZoom zoom)
      (.setName "main")))

  (let [{:keys [position bounds zoom]} game-dimensions/mini-camera]
    (->
      (.. game -cameras)
      (.add (:x position) (:y position) (:width position) (:height position))
      (.setBackgroundColor 0x222222)
      (.setBounds (:x bounds) (:y bounds) (:width bounds) (:heigh bounds))
      (.setZoom zoom)
      (.setName "mini")
      (.setScroll 0 0))))

(defn cabin-current-y [game]
  (.. (get-in @state [:objects :cabin]) -y))

(defn go-to-level
  [game level]
  (let [cabin   (get-in @state [:objects :cabin])
        physics (.. game -physics)
        {:keys [x y]} (game-dimensions/cabin-at-level level)
        upward? (neg? (- y (cabin-current-y game)))]
    (.moveTo physics cabin x y)
    (swap! state assoc :cabin-current-move {:destination-y y :upward? upward?})))

(defn stop-cabin
  [game]
  (let [cabin (get-in @state [:objects :cabin])]
    (.setVelocityY cabin 0)
    (swap! state dissoc :cabin-current-move)))

(defn command-handler
  [game [type params]]
  (case type
    :command/go-to-level
    (go-to-level game (:level params))))

(defn- process-cabin-commands
  [game]
  (doseq [command (elevator-cabin/get-commands (:cabin @state))]
    (command-handler game command))
  (swap! state update :cabin elevator-cabin/commands-processed))

(defn add-worker
  [game {:keys [name current-level id] :as worker}]
  (let [{:keys [x y]} (game-dimensions/worker-at-level current-level)
        worker-sprite (-> (.. game -add)
                          (.image 0 0 "female0")
                          (.setName name)
                          (.setOrigin 0 0)
                          (.setPosition x y))]
    (swap! state assoc-in [:objects :workers id] worker-sprite)))

(defn worker-command-handler
  [game [command-type :as command]]
  (case command-type
    :added-worker (add-worker game command)))

(defn- process-workers-commands
  [game]
  (doseq [command (workers/get-commands (:workers @state))]
    (worker-command-handler game command))
  (swap! state update :workers workers/commands-processed))

(defn- arrived-at-destination?
  [{:keys [destination-y upward?] :as move} current-y]
  (if upward? (<= current-y destination-y) (>= current-y destination-y)))

(defn update-game [game]
  (swap! state update :ticks inc)

  (doto
    (.. game -cameras -main)
    (.setScroll 0 (:ticks @state)))

  (process-cabin-commands game)

  (when-let [move (get-in @state [:cabin-current-move])]
    (when (arrived-at-destination? move (cabin-current-y game))
      (do (stop-cabin game)
          (emit-cabin-command! [:command/cabin-arrived]))))

  (process-workers-commands game))

(def config
  (clj->js
    {:type    js/Phaser.AUTO
     :width   game-dimensions/screen-width
     :height  game-dimensions/screen-height
     :physics {:default "arcade"
               :arcade  {:gravity {:y 0}
                         :debug   true}}
     :scene   {:preload #(this-as game (preload game))
               :create  #(this-as game (create game))
               :update  #(this-as game (update-game game))}}))

(defonce game (js/Phaser.Game. config))

(defn spawn-random-worker
  []
  (println "xouxou")
  (emit-worker-command!
    [:spawn (workers/->random-worker (:ticks @state) game-dimensions/nb-levels (constantly 42))]))
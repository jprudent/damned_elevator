(ns damel.browser
  (:require [damel.game-dimensions :as game-dimensions]
            [damel.model.workers :as workers]
            [damel.state :as state]
            [damel.cabin :as cabin]
            [damel.workers :as gworkers]
            [damel.game-objects-factory :as gof]))

(enable-console-print!)

(defn update-game [state game]
  (-> state
      (update :ticks inc)
      (cabin/process-commands game)
      (cabin/control-move game)
      (gworkers/process-commands game)
      (gworkers/control-move game)))

(defonce state (atom state/state-0))

(def config
  (clj->js
    {:type     js/Phaser.AUTO
     :width    game-dimensions/screen-width
     :height   game-dimensions/screen-height
     :pixelArt true
     :physics  {:default "arcade"
                :arcade  {:gravity {:y 0}
                          :debug   true}}
     :scene    {:preload #(this-as game (gof/preload-assets game))
                :create  #(this-as game (swap! state gof/create-building game))
                :update  #(this-as game (swap! state update-game game))}}))

(defonce game (js/Phaser.Game. config))

;; some commands utilities
;; for interacting with the game
;; -----------------------

(defn- random-worker []
  (workers/->random-worker (:ticks @state)
                           game-dimensions/nb-levels
                           (constantly 42)))

(defn spawn-random-worker [] (swap! state state/emit-worker-command [:spawn (random-worker)]))

(defn enter-elevator [worker-id] (swap! state state/emit-worker-command [:enter-elevator worker-id]))
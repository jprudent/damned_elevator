(ns damel.game-dimensions)

(def screen-height 600)
(def screen-width 800)

(def floor-height 10)
(def floor-width 600)

(def level-text-height 14)
(def level-text-width 100)

(def elevator-height 166)
(def elevator-width 192)

(def cabin-width 236)
(def cabin-height 158)

(def nb-levels 3)

(def level-height 200)

(def world-height (* nb-levels level-height))
(def world-width 700)


(def worker-sprite-height 218)
(def worker-sprite-width 148)
(def worker-scale (/ 140 218))
(def worker-height (* worker-sprite-height worker-scale))
(def worker-width (* worker-sprite-width worker-scale))

;; todo could be memoized
(defn level
  "return dimension and positions of static things at given level assuming top-left
  corner is 0 0."
  [level-number]
  {:pre [(< level-number nb-levels)]}
  (let [floor-y-min (- world-height (* level-number level-height))
        floor-y     (- floor-y-min floor-height)]
    {:floor      {:x      0
                  :y      floor-y
                  :width  floor-width
                  :height floor-height}
     :elevator   {:x      (/ (- cabin-width elevator-width) 2)
                  :y      (- floor-y elevator-height)
                  :width  elevator-width
                  :height elevator-height}
     :level-text {:x      610
                  :y      (- floor-y-min level-text-height)
                  :width  level-text-width
                  :height level-text-height}}))

(def cabin
  {:x      0
   :y      (- world-height floor-height cabin-height)
   :width  cabin-width
   :height cabin-height})

(defn cabin-at-level
  "Coordinates of the cabin for given level (assuming anchor is 0 0)"
  [level-number]
  (assoc cabin :y (- (get-in (level level-number) [:floor :y])
                     cabin-height)))

(defn worker-at-level
  "Coordinates of the worker for given level (assuming anchor is 0 0)"
  [level-number]
  {:x (- world-width worker-width)
   :y (- (get-in (level level-number) [:floor :y]) worker-height)
   :height worker-height
   :width worker-width
   :scale worker-scale})

(def main-camera
  {:position {:x 0 :y 0 :width world-width :height screen-height}
   :bounds   {:x 0 :y 0 :width world-width :height world-height}
   :zoom     1})

(def mini-camera
  (let [width 100]
    {:position {:x world-width :y 0 :width width :height screen-height}
     :bounds   {:x 0 :y 0 :width world-width :height world-height}
     :zoom     (/ width (float world-width))}))
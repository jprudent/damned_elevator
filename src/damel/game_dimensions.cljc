(ns damel.game-dimensions)

(def screen-height 600)
(def screen-width 800)

(def floor-height 10)
(def floor-width 600)

(def level-text-height 14)
(def level-text-width 100)

(def elevator-height 166)
(def elevator-width 225)

(def nb-levels 4)

(def level-height 200)

(def world-height (* nb-levels level-height))
(def world-width 700)


(defn level
  "return dimension and positions of things at given level assuming right-left
  corner is 0 0."
  [level-number]
  {:pre [(< level-number nb-levels)]}
  (let [floor-y-min (- world-height (* level-number level-height))
        floor-y     (- floor-y-min floor-height)]
    (prn floor-y)
    {:floor      {:x      0
                  :y      floor-y
                  :width  floor-width
                  :height floor-height}
     :elevator   {:x      0
                  :y      (- floor-y elevator-height)
                  :width  elevator-width
                  :height elevator-height}
     :level-text {:x      610
                  :y      (- floor-y-min level-text-height)
                  :width  level-text-width
                  :height level-text-height}}))

(def main-camera
  {:position {:x 0 :y 0 :width world-width :height screen-height}
   :bounds   {:x 0 :y 0 :width world-width :height world-height}
   :zoom     1})


(def mini-camera
  (let [width 100]
    {:position {:x world-width :y 0 :width width :height screen-height}
     :bounds   {:x 0 :y 0 :width world-width :height world-height}
     :zoom     (/ width (float world-width))}))
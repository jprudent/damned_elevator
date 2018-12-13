(ns damel.phaser-utils
  "contains all interop thing")

(defn text-at
  "Print text (useful for debug)"
  [game x y height text]
  (-> (.. game -add)
      (.text x y text
             #js {:fontSize (str height "px") :fill "#0F0"})))

;; getters
(defn x [o] (.. o -x))
(defn y [o] (.. o -y))
(defn displayWidth [o] (.. o -displayWidth))
(defn physics [o] (.. o -physics))

;; setters
(defn set-velocity [o velocity] (.setVelocityY o velocity))


;; function wrappers
(defn move-to [game o x y] (.moveTo (physics game) o x y))

;; utils
(defn get-obj-coors
  [o]
  {:x     (x o)
   :y     (y o)
   :width (displayWidth o)})
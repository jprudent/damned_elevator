(ns damel.kinetic
  "utilities about moving things")

(defn- arrived? [lte? x1 x2] (if lte? (<= x1 x2) (>= x1 x2)))

(defn arrived-at-destination-y?
  [{:keys [destination-y upward?]} {:keys [y]}]
  (arrived? upward? y destination-y))

(defn arrived-at-destination-x?
  [{:keys [destination-x to-left?]} {:keys [x]}]
  (arrived? to-left? x destination-x))
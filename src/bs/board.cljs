(ns bs.board
  "This namespace provides a model of a board, which is represented as
  a bi-directional graph of cells. Walls are represented as cells
  having no connection to any neighbors.

  The data representation used for this board might look like this,
  given the following board:

  S = Source
  T = Target
  W = Wall   (no connections to neighbors)

       0   1   2
     +---+---+---+
   0 | S |   |   |
     +---+---+---+
   1 | W | W | T |
     +---+---+---+

  ``` clojure
  {:board/width 3
   :board/height 2
   :board/source [0 0]
   :board/target [2 1]
   :board/edges {[0 0] #{[0 1]}
                 [1 0] #{[0 0] [2 0]}
                 [2 0] #{[1 0] [2 1]}
                 [0 1] #{}
                 [1 1] #{}
                 [2 1] #{[2 0]}}}
  ```"
  (:require [clojure.set :as set]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Board graph

(defn- in-board? [width height [x y]]
  (and (< -1 x width)
       (< -1 y height)))

(defn- adjacent-coords [width height [x y]]
  (set/select #(in-board? width height %)
              #{[(inc x) y]
                [(dec x) y]
                [x (inc y)]
                [x (dec y)]}))

(defn- make-edges [width height]
  (into {} (for [x (range width)
                 y (range height)
                 :let [coord [x y]]]
             [coord (adjacent-coords width height coord)])))

(defn make [width height]
  {:board/width width
   :board/height height
   :board/source nil
   :board/target nil
   :board/walls #{}
   :board/edges (make-edges width height)})

(defn all-coordinates [board]
  (keys (:board/edges board)))

(defn neighbor-coords [board pos]
  (get-in board [:board/edges pos]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Queries

(defn source? [board pos]
  (= (:board/source board) pos))

(defn target? [board pos]
  (= (:board/target board) pos))

(defn wall? [board pos]
  (= (get-in board [:board/edges pos]) #{}))

(def connected? (complement wall?))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Mutations

(defn set-source [board pos]
  (assoc board :board/source pos))

(defn set-target [board pos]
  (assoc board :board/target pos))

;; walls

(defn- update-edge [board p1 p2 f]
  (-> board
      (update-in [:board/edges p1] f p2)
      (update-in [:board/edges p2] f p1)))

(defn- connected-neighbor-coords [{:board/keys [width height] :as board} pos]
  (set/select #(connected? board %) (adjacent-coords width height pos)))

(defn- update-walls [board pos f]
  (reduce #(update-edge %1 %2 pos f) board (connected-neighbor-coords board pos)))

(defn make-wall [board pos]
  (update-walls board pos disj))

(defn destroy-wall [board pos]
  (update-walls board pos conj))

(ns bs.core
  (:require [bs.events]
            [bs.state :as state]
            [bs.board :as board]
            [reagent.core :as r]))

(defn cell-classes [board pos]
  [(when (board/wall? board pos) "cell--wall-animated")
   (when (board/target? board pos) "cell--target")
   (when (board/source? board pos) "cell--source")])

(defn cell [board pos]
  [:td.cell
   {:on-mouse-over #(state/dispatch [:event/hover-cell pos])
    :on-mouse-down #(state/dispatch [:event/start-dragging pos])
    :on-mouse-up #(state/dispatch [:event/stop-dragging pos])
    :class (cell-classes board pos)}])

(defn root [db]
  (let [board (:db/board @db)]
    [:table
     [:tbody
      (for [x (range (:board/width board))]
        [:tr {:key x}
         (for [y (range (:board/height board))]
           ^{:key y} [cell board [x y]])])]]))

(defn ^:dev/after-load render! []
  (state/dispatch [:event/start])
  (r/render [root state/app-db] (.getElementById js/document "app")))

(def main! render!)

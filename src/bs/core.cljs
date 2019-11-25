(ns bs.core
  (:require [bs.events]
            [bs.state :as state]
            [bs.board :as board]
            [reagent.core :as r]))

(defn board []
  (:db/board @state/app-db))

(defn board-size []
  (select-keys @(r/track board) [:board/width :board/height]))

(defn cell-classes [pos]
  (let [board @(r/track board)]
    [(when (board/wall? board pos) "cell--wall-animated")
     (when (board/target? board pos) "cell--target")
     (when (board/source? board pos) "cell--source")]))

(defn cell [pos]
  [:td.cell
   {:on-mouse-over #(state/dispatch [:event/hover-cell pos])
    :on-mouse-down #(state/dispatch [:event/start-dragging pos])
    :on-mouse-up #(state/dispatch [:event/stop-dragging pos])
    :class @(r/track cell-classes pos)}])

(defn root []
  (let [{:board/keys [width height]} @(r/track board-size)]
    [:table
     [:tbody
      (for [x (range width)]
        [:tr {:key x}
         (for [y (range height)]
           ^{:key y} [cell [x y]])])]]))

(defn ^:dev/after-load render! []
  (state/dispatch [:event/start])
  (r/render [root] (.getElementById js/document "app")))

(def main! render!)

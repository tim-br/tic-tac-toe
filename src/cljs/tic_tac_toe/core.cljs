(ns tic-tac-toe.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [chan put! sub <!] :as async]
            [om-tools.core :refer-macros [defcomponent defcomponentk]]))

(enable-console-print!)

(defonce app-state (atom {:text "Hello Chestnut!"}))

(defn x-cord
  [pair]
  (get-in pair [0]))

(defn y-cord
  [pair]
  (get-in pair [1]))


(defcomponent cell [data owner {:keys [pos]}]
  (init-state [_]
              {:clicked false})
  (render-state [this {:keys [info]}]
          (dom/td #js {:className "cell"
                       :id (if (= true (om/get-state owner :clicked))
                             "light-blue"
                             data)
                       :onClick #(do
                                   (om/update-state! owner :clicked not)
                                   (put! info pos)
                                   #_(js/console.log
                                    "the x y cords is " (x-cord pos) ", " (y-cord pos)))})))

(defn cell-col
  [x y]
  (if (even? x)
    (if (even? y)
      "brown"
      "light-brown")
    (if (even? y)
      "light-brown"
      "brown")))

(defcomponent board [data owner]
    (init-state [_]
      {:x-cell (range 3), :y-cell (range 3)})
    (render-state [_ state]
      (apply dom/table #js {:id "chess-board"}
        (for [y (:x-cell state)]
          (apply dom/tr nil
                 (for [x (:y-cell state)]
                   (om/build cell (cell-col x y)
                             {:opts {:pos [x y]}
                              :init-state {:info (:info state)}})))))))

(defcomponent main-view
  [data owner]
  (init-state [_]
    {:info (chan)
     :req-chan     (chan)
     :pub-chan     (chan)})

  (will-mount [_]
      (let [info (om/get-state owner :info)]
        (go (loop []
              (let [pos (<! info)]
                (js/console.log "the cords are " (x-cord pos) ", " (y-cord pos))
                (recur))))))

  (render-state [_ {:keys [info]}]
                (when info
                  (js/console.log "info tnsieraotn"))
    (dom/div nil
        (om/build board nil {:init-state {:info info}})
        (om/build board nil {:init-state {:info info}})
        (om/build board nil {:init-state {:info info}}))))

(defcomponent my-test
  [data owner]
  (render [_]
          (dom/h1 nil "hello world")))

(defn main []
  (om/root
    (fn [app owner]
      (reify
        om/IRender
        (render [_]
                (dom/div nil
                         (dom/h1 nil (:text app))
                         (om/build my-test nil)
                         ;(om/build board nil)
                         (om/build main-view nil)))))
    app-state
    {:target (. js/document (getElementById "app"))}))

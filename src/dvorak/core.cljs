(ns dvorak.core
  (:require cljs.pprint
            [reagent.core :as r]
            [reagent.dom :as rd]
            [dvorak.translator :as d]))

(def state
  (r/atom {:input ""
           :output ""
           :tr ""}))

(defn root-component
  [input-ratom output-ratom tr-ratom dest-atom]
  [:div
   [:h3 "This is your input textbox."]
   [:textarea
    {:value @input-ratom
     :on-change (fn [new]
                  (let [new-txt (.-value (.-target new))]
                    (reset! input-ratom new-txt)
                    (reset! output-ratom (d/convert-to-dvorak new-txt))))}
    ]
   [:h3 "This is your QWERTY=>Dvorak simulated text"]
   [:textarea
    {:value @output-ratom
     :disabled true}]
   [:h3 "Try entering the text above into this textbox .."]
   [:textarea
    {:value @tr-ratom
     :on-change (fn [evt]
                  (let [new-txt (.-value (.-target evt))]
                    (reset! tr-ratom new-txt)
                    (reset! dest-atom (d/simulate-dvorak new-txt)))                  )
     }]
   [:h3 ".. which is as if you typed the original input in Dvorak"]
   [:textarea
    {:value @dest-atom
     :disabled true}
    ]
   ])

(defn mountit []
  (rd/render [root-component
              (r/cursor state [:input])
              (r/cursor state [:output])
              (r/cursor state [:tr])
              (r/cursor state [:enlightenment])]
             (.getElementById js/document "app")))


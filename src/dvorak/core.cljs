(ns dvorak.core
  (:require cljs.pprint
            [reagent.core :as r]
            [reagent.dom :as rd]
            [dvorak.translator :as d]))

(def state
  (r/atom {:input   ""
           :output  ""
           :tr      ""
           :table   nil
           :r-table nil}))

(defn kb-selector
  [table r-table]
  (let [choices [:dvorak :colemak]]
    (r/create-class {:component-did-mount
                     (fn []
                       (reset! table (d/make-table (first choices) :qwerty))
                       (reset! r-table (d/make-table :qwerty (first choices)))
                       )
                     :reagent-render
                     (fn [table r-table]
                       [:select.form-control {:field     :list
                                              :id        :many.options
                                              :on-change (fn [new]
                                                           (let [to-kb     (.-value (.-target new))
                                                                 new-table (d/make-table (keyword to-kb) :qwerty)]
                                                             (reset! table new-table)
                                                             (reset! r-table (d/reverse-table new-table))))}
                        (for [x choices]
                          [:option {:key x} (name x)])]
                       )})))

(defn translator
  [table input-ratom output-ratom]
  [:div
   [:h3 "This is your input textbox."]
   [:textarea
    {:value     @input-ratom
     :on-change (fn [new]
                  (let [new-txt (.-value (.-target new))]
                    (reset! input-ratom new-txt)))}
    ]
   [:h3 "This is your QWERTY=>Dvorak simulated text"]
   [:textarea
    {:value    (let [o (d/convert @table @input-ratom)]
                 (reset! output-ratom o)
                 )
     :disabled true}]]
  )

(defn magic-input
  [r-table tr-ratom dest-atom]
  [:div
   [:h3 "Try entering the text above into this textbox .."]
   [:textarea
    {:value     @tr-ratom
     :on-change (fn [evt]
                  (let [new-txt (.-value (.-target evt))]
                    (reset! tr-ratom new-txt)))
     }]
   [:h3 ".. which is as if you typed the original input in Dvorak"]
   [:textarea
    {:value    (let [d (d/convert @r-table @tr-ratom)]
                 (reset! dest-atom d))
     :disabled true}
    ]])

(defn root-component
  [table r-table input-ratom output-ratom tr-ratom dest-atom]
  (r/create-class
   {:component-did-mount
    (fn []
      (add-watch table
                 :clear-tr-on-table-change
                 (fn [k r o n]
                   (reset! tr-ratom ""))))
    :reagent-render
    (fn [table r-table input-ratom output-ratom tr-ratom dest-atom]
      [:div
       [:h3 "option list"]
       [:div.form-group
        [:label "pick a source keyboard"]
        [kb-selector table r-table]        ]
       [translator table input-ratom output-ratom]
       [magic-input r-table tr-ratom dest-atom]
       ])}))

(defn mountit []
  (rd/render [root-component
              (r/cursor state [:table])
              (r/cursor state [:r-table])
              (r/cursor state [:input])
              (r/cursor state [:output])
              (r/cursor state [:tr])
              (r/cursor state [:enlightenment])]
             (.getElementById js/document "app")))


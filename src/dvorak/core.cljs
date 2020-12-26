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
           :r-table nil
           :active-keyboard nil}))

(defn kb-selector
  [table r-table active-keyboard]
  (let [choices [:dvorak :colemak]]
    (r/create-class {:component-did-mount
                     (fn []
                       (let [c (first choices)]
                         (reset! table (d/make-table c :qwerty))
                         (reset! r-table (d/make-table :qwerty c))
                         (reset! active-keyboard c))
                       )
                     :reagent-render
                     (fn [table r-table]
                       [:select.form-control {:field     :list
                                              :id        :many.options
                                              :on-change (fn [new]
                                                           (let [to-kb     (.-value (.-target new))
                                                                 new-table (d/make-table (keyword to-kb) :qwerty)]
                                                             (reset! table new-table)
                                                             (reset! r-table (d/reverse-table new-table))
                                                             (reset! active-keyboard to-kb)))}
                        (for [x choices]
                          [:option {:key x} (name x)])]
                       )})))

(defn translator
  [table input-ratom output-ratom active-keyboard]
  [:div
   [:h3 "This is your input textbox."]
   [:textarea
    {:value     @input-ratom
     :on-change (fn [new]
                  (let [new-txt (.-value (.-target new))]
                    (reset! input-ratom new-txt)))}
    ]
   [:h3 "This is your QWERTY=>" @active-keyboard " simulated text"]
   [:textarea
    {:value    (let [o (d/convert @table @input-ratom)]
                 (reset! output-ratom o)
                 )
     :disabled true}]]
  )

(defn magic-input
  [r-table tr-ratom dest-atom active-keyboard]
  [:div
   [:h3 "Try entering the text above into this textbox .."]
   [:textarea
    {:value     @tr-ratom
     :on-change (fn [evt]
                  (let [new-txt (.-value (.-target evt))]
                    (reset! tr-ratom new-txt)))
     }]
   [:h3 ".. which is as if you typed the original input in " @active-keyboard]
   [:textarea
    {:value    (let [d (d/convert @r-table @tr-ratom)]
                 (reset! dest-atom d))
     :disabled true}
    ]])

(defn root-component
  [table r-table input-ratom output-ratom tr-ratom dest-atom active-keyboard]
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
        [kb-selector table r-table active-keyboard]]
       [translator table input-ratom output-ratom active-keyboard]
       [magic-input r-table tr-ratom dest-atom active-keyboard]
       ])}))

(defn mountit []
  (rd/render [root-component
              (r/cursor state [:table])
              (r/cursor state [:r-table])
              (r/cursor state [:input])
              (r/cursor state [:output])
              (r/cursor state [:tr])
              (r/cursor state [:enlightenment])
              (r/cursor state [:active-keyboard])]
             (.getElementById js/document "app")))


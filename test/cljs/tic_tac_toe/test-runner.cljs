(ns tic_tac_toe.test-runner
  (:require
   [cljs.test :refer-macros [run-tests]]
   [tic_tac_toe.core-test]))

(enable-console-print!)

(defn runner []
  (if (cljs.test/successful?
       (run-tests
        'tic_tac_toe.core-test))
    0
    1))

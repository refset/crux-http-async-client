(ns crux-http-async-client.test-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [crux-http-async-client.core-test]))

(doo-tests 'crux-http-async-client.core-test)

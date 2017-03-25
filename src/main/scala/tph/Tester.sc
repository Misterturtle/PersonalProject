val test = List[(String, Int)](
  ("a", 1),
  ("b", 5),
  ("c", 3),
  ("d", 7),
  ("e", 1)
)

test.reduceLeft((mpv, current) => if(mpv._2 > current._2) mpv else current)
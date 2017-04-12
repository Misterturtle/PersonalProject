import tph.Constants.ActionVotes.CardPlay



val list1 = List("a","b","c","d","e","f", "e")
val list2 = List("a", "b", 2, "e")

list1 match{
  case a :: b :: c :: d :: e :: f :: g =>
    if(a == list2.head && b == list2(1))
      3




}





/*
RULES:
1.) Find the most popular individual vote (e - 172)
2.) Find the most popular next order vote containing the lower order vote (e, f - 86)
3.) If the higher order vote is 50% or more than the lower order vote, repeat steps 2 and 3 (True)
------2-2.)  Find the most popular next order vote containing the lower order vote (e, f, b - 40)
------3.2) If the higher order vote is 50% or more than the lower order vote, repeat steps 2 and 3 (False)
**Logically at this point, we have the most popular pattern.
**That pattern should now be considered a unique vote that is different from the parts that make it up (See "Votes with EF:" section)
4.) Out of all votes that contain the deduced vote, find the most popular individual vote(b - 72)
5.) Out of all the votes that contain the deduced vote, find the most popular next order vote containing the lower order vote (b, h - 21)
6.) If the higher order vote is 50% or more than the lower order vote, repeat steps 4 and 5 (False)
**Logically at this point, we have the two most popular patterns.
**Now we need to figure out how to combine them, or even if we should.
7.)If the 2nd pattern is 50% or more than the first pattern, continue, else the first pattern is the final vote.
8.)Find the most popular position of the 2nd pattern compared to the first(b is +1 of e,f)
9.)Repeat 4-6 with this new deduced vote order
------4.2) (h-21)
------5.2) (Nothing)
------6.2) (False)



 */

/*
Votes with EF(X) and B(Y):
-X, Y, h (21)
-Y, X, h  (17)
-Y, X (15)
-X, Y (19)

X +1 = 21 + 19 = 40
X -1 = 17 +15 = 32


 */

/*
Votes with EF:
-X, b, h (21)
-b, X, h  (17)
-b, X (15)
-X, b (19)
-c, X (5)
-d, X (9)

b, h = 21

a=0
b=72
c=5
d=9
e=0
f=0
h=38

 */




/*WITH EF:
EF = 86
a=
b= 21 + 17 +15 + 19 = 72
c= 5
d= 9
e=
f=
h= 21 + 17 = 38

WITH E:
a=8 12 10 12 14 22 2 2 2 1 1 = 86
b=21 17 15 19 8 12 10 12 14 = 128
c=5 2 1 = 8
d=9 2 1 = 12
e= 0
f=21 17 15 19 5 9 = 86
h=21 17 = 38

*/

/*


b, h = 21
f, b = 21 + 19 = 40
b, e = 17 +15 = 32




-e, f, b, h (21)
-b, e, f, h  (17)
-b, e, f (15)
-e, f, b (19)
-e, a, b (8)
-e, b, a (12)
-b, e, a (10)
-a, b, e (12)
-b, a, e (14)
-a, e (22)
-a, b (8)
-b, a (10)
-c, e, f (5)
-d, e, f (9)
-a (2)
- c, a (2)
-a, c (2)
-d, a (2)
-a, d (2)
-d, e, a (2)
-c, e, a (2)
-e, a (2)
-c, a, e (1)
-a, d, e (1)


e, f, b, h = 21
b, e, f, h = 17

f, b, h = 21
e, f, b = 40
e, a, b = 8
e, b, a = 12
d, e, a = 2
d, e, f = 9
c, a, e = 1
c, e, a = 2
c, e, f = 5
b, a, e = 14
b, e, a = 10
b, e, f = 32
a, b, e = 12
a, d, e = 1

96




f,b = 34
f, h = 17
e, f = 86
e,a = 24
e,b = 12
d, a = 2
d, e = 12
c, a = 3
c, e = 7
b, a = 36
b, e = 54
b, h = 21
a, b =  28
a, c = 2
a, d = 3
a, e = 37

a= 24 + 37
b= 12 + 54
c= 7
d= 12
e= 0
f= 86 +
h= 0





a = 114
b = 146
c = 12
d = 16
e = 172
f = 86
h = 38



  */
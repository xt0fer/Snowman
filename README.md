# Snowman
a Very Simple compiler written in Java. produces code for the Zee virtual machine.

This is based on the-super-tiny-compiler written in javascript by @jamiebuilds (see [the-super-tiny-compiler](https://github.com/jamiebuilds/the-super-tiny-compiler))

Tranliterated into Java by @xt0fer for zip code wilmington lab on compiler phases and the ideas of scanning and parser into trees (well, ASTs).

It also has only three phases, not four like in the original. 

The first phase is the tokenizer, the second is the parser, and the  third is code generation.

Right now the `snowman language` is very simple.

It has two functions which add or subtract integers.

```
(add 2 (subtract 4 2)) 
```

produces a pseudo-assembly-code of 

```
;; Begin program code
                START
                PUSH #2
                PUSH #4
                PUSH #2
                SUBTRACT
                ADD
                PRINT
                HALT
```

Lines which start with `;;` are comments.

which will be used to demonstrate the `Zee Virtual Machine` (zeeVM). See https://github.com/xt0fer/ZeeVM


```
(add 1 1)
```

```
(add 45 (add (subtract (add 21 21) 13) 23))
```

are examples of other possible programs in this language.

## What you need to do

Once you have read the code and get an idea of what it does, you need to add the following concepts to it:

- now, you add `multiply` and `divide`
- how about comments like "// comments..." (from // to EOL)
- simple comparisons `EQ` `NE` `ZE` `LT` and `GE`
- a `LET` function

## Furture Lab Ideas

Below, where you see "LET" think "let". Uppercase, lowercase should not matter.

### Variables
Integer values mapped to names.
```
(LET X 5)
(LET Y (add 5 8))
(add X Y)
```

Could then
```
(LET TRUE 1)
(LET FALSE 0)
```

might also 

```
(LT 4 5) -> 1
(GE 4 5) -> 0
(GE 3 3) -> 1
(GE 5 4) -> 1
(EQ 5 5) -> 1
(NE 4 5) -> 1
(NE 3 3) -> 1
(EQ 1 x) -> isTrue
(EQ 0 x) -> isFalse
```

and also
```
(IF 1 (add 4 5))
```

and what about 

```
(LAMBDA (X Y) (...))
(λ (X Y) (...))

```
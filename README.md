# Fetch Points
FetchPoints is a program that assigns a payer to each point earned by the user and spends points in the order of the oldest timestamp. This README provides instructions on how to compile and run the program.

## Author
Thomas Lesniak
tjlesniak@wisc.edu

## Prerequisites
The program requires Java 8 or later to be installed on your machine.

## Installation
1.  Clone the repository 

2.  Open the terminal and navigate to the project directory.

3.  Compile the program by running the following command:

```
javac FetchPoints.java
```

## Usage
1.   Run the program by running the following command:

```
java FetchPoints [points to spend] [csv file path]
```

2.  Replace [points to spend] with the number of points you want to spend and [csv file path] with the path to the CSV file containing the transactions.


The program reads through the transactions from the CSV file and assigns a payer to each point earned by the user. Points are then spent in the order of the oldest timestamp (oldest transactions have first priority). The program outputs the remaining balances for each payer after the points have been spent.

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <conio.h>

typedef struct Transaction {
    float amount;
    struct Transaction* next;
} Transaction;

typedef struct TransactionNode {
    Transaction* top;
} TransactionNode;

typedef struct {
    char name[50];
    int accountNumber;
    float balance;
    TransactionNode* transactionHistory; 
} Customer;


typedef struct Node {
    Customer customer;
    struct Node* next;
} Node;


Node* accounts = NULL;

void displayEmployeeMenu();
void displayCustomerMenu();
// 
Node* createAccount(char name[], int accountNumber, float initialBalance);
void sortAccounts(Node* accounts);
Node* searchAccountByNumber(Node* accounts, int accountNumber);
void deleteAccount(Node** accounts, int accountNumber);
// 
void addMoney(Node* account, int accountNumber, float amount);
void withdrawMoney(Node* account, int accountNumber, float amount);
float viewBalance(Node* account, int accountNumber);
void viewTransactionHistory(Node* account, int accountNumber);
void pushTransaction(Customer* customer, float amount);
float popTransaction(Customer* customer);
// 
int validatePositiveFloat(const char *input, float *result);
int validatePositiveInteger(const char *input, int *result);
int validateInput(const char *input);
void flushInputBuffer() {
    int c;
    while ((c = getchar()) != '\n' && c != EOF);
}
// 
int accountNumberCounter = 1001;  

int accountNumber;
float amount;

int main() {

    char char_choice[1];
    int choice;
    int loop = 1;
    while(loop) {
        printf("Select user type:\n");
        printf("1. Employee\n");
        printf("2. Customer\n");
        printf("3. Exit\n");
        printf("Enter your choice: ");
        scanf("%s", &char_choice);
        choice = atoi(char_choice);
        if(!(choice > 0 && choice < 4)){
            printf("Invalid choice. Please try again.\n");

        }
        else{
            switch (choice) {
                case 1:
                    displayEmployeeMenu();
                    break;
                case 2:
                    displayCustomerMenu();
                    break;
                case 3:
                    printf("Exiting program.\n");
                    loop = 0;
                default:
                    break;
            }
        }
    }

    return 0;
}

void displayEmployeeMenu() {
    char char_choice[1];
    int choice;
    int loop = 1;
    char input[100];
    int status;

    while (loop) {
        printf("\nBank Management System Menu\n");
        printf("1. Create Account\n");
        printf("2. View Accounts\n");
        printf("3. Search Account\n");
        printf("4. Delete Account\n");
        printf("5. Exit\n");
        printf("Enter your choice: ");
        scanf("%s", &char_choice);
        choice = atoi(char_choice);
        if(!(choice > 0 && choice < 6)){
            printf("Invalid choice. Please try again.\n");
        }
        else{
            switch (choice) {
                case 1: {
                    char name[50];
                    printf("Enter customer name: ");
                    flushInputBuffer();
                    fgets(name, sizeof(name), stdin);
                    status = validateInput(name);
                    if(status == 0){
                        Node* newAccount = createAccount(name, accountNumberCounter++, 0.0);
                        if (newAccount != NULL) {
                            newAccount->next = accounts;
                            accounts = newAccount;
                            printf("Account number: %d\n",accountNumberCounter - 1);
                            printf("Account created successfully!\n");
                        } else {
                            printf("Error creating account. Please try again.\n");
                        }
                    }
                    break;
                }
                case 2: {
                    sortAccounts(accounts);
                    break;
                }
                case 3: {
                    printf("Enter account number to search: ");
                    flushInputBuffer();
                    fgets(input, sizeof(input), stdin);
                    status = validatePositiveInteger(input, &accountNumber);
                    if(status == 0){
                        Node* result = searchAccountByNumber(accounts, accountNumber);
                        if (result != NULL) {
                            printf("Account found:\n");
                            printf("Name: %s\n", result->customer.name);
                            printf("Balance: $%.2f\n", result->customer.balance);
                        } else {
                            printf("Account not found.\n");
                        }
                    }
                    break;
                }
                case 4: {
                    printf("Enter account number to delete: ");
                    flushInputBuffer();
                    fgets(input, sizeof(input), stdin);
                    status = validatePositiveInteger(input, &accountNumber);
                    if(status == 0){
                        deleteAccount(&accounts, accountNumber);
                    }
                    break;
                }
                case 5:
                    printf("Exiting...\n");
                    loop = 0;
                default:
                    break;
            }
        }
    }
}

void displayCustomerMenu() {
    char char_choice[1];
    int choice;
    int loop = 1;
    char input[100];
    char balance[10];
    int status;
    int bal_status;
    while(loop) {
        printf("1. Add Money\n");
        printf("2. Withdraw Money\n");
        printf("3. View Balance\n");
        printf("4. View Transaction History\n");
        printf("5. Exit\n");
        printf("Enter your choice: ");
        scanf("%s", &char_choice);
        choice = atoi(char_choice);
        if(!(choice > 0 && choice < 6)){
            printf("Invalid choice. Please try again.\n");
        }
        else{
            switch (choice) {
                case 1:
                    printf("Enter account number: ");
                    flushInputBuffer();
                    fgets(input, sizeof(input), stdin);
                    printf("Enter amount to add: ");
                    fgets(balance, sizeof(balance), stdin);
                    status = validatePositiveInteger(input, &accountNumber);
                    bal_status = validatePositiveFloat(balance, &amount);
                    if(status == 0 && bal_status == 0){
                        addMoney(accounts, accountNumber, amount);
                    }
                    break;
                case 2:
                    printf("Enter account number: ");
                    flushInputBuffer();
                    fgets(input, sizeof(input), stdin);
                    printf("Enter amount to Withdraw: ");
                    fgets(balance, sizeof(balance), stdin);
                    status = validatePositiveInteger(input, &accountNumber);
                    bal_status = validatePositiveFloat(balance, &amount);
                    if(status ==0 && bal_status == 0){
                        withdrawMoney(accounts, accountNumber, amount);
                    }
                    break;
                case 3:
                    printf("Enter account number to view balance: ");
                    flushInputBuffer();
                    fgets(input, sizeof(input), stdin);
                    status = validatePositiveInteger(input, &accountNumber);
                    if(status == 0){                    
                        printf("Current balance: $%.2f\n", viewBalance(accounts, accountNumber));
                    }
                    break;
                case 4:
                    printf("Enter account number for transaction history: ");
                    flushInputBuffer();
                    fgets(input, sizeof(input), stdin);
                    status = validatePositiveInteger(input, &accountNumber);
                    if(status == 0){
                        viewTransactionHistory(accounts, accountNumber);
                    }
                    break;
                case 5:
                    printf("Exiting...\n");
                    loop = 0;
                default:
                    break;
            }
        }
    }

}

Node* createAccount(char name[], int accountNumber, float initialBalance) {
    Node* newNode = (Node*)malloc(sizeof(Node));
    if (newNode != NULL) {
        strcpy(newNode->customer.name, name);
        newNode->customer.accountNumber = accountNumber;
        newNode->customer.balance = initialBalance;

        newNode->customer.transactionHistory = (TransactionNode*)malloc(sizeof(TransactionNode));
        if (newNode->customer.transactionHistory != NULL) {
            newNode->customer.transactionHistory->top = NULL;
            newNode->next = NULL;
        } else {
            free(newNode);
            return NULL; 
        }
    }
    return newNode;
}

void addMoney(Node* accounts, int accountNumber, float amount) {
    Node* current = accounts;
    while (current != NULL) {
        if (current->customer.accountNumber == accountNumber) {
            current->customer.balance += amount;
            pushTransaction(&(current->customer), amount);
            printf("Amount added successfully!\n");
            return;
        }
        current = current->next;
    }
    printf("Account not found.\n");
}

void withdrawMoney(Node* accounts, int accountNumber, float amount) {
    Node* current = accounts;
    while (current != NULL) {
        if (current->customer.accountNumber == accountNumber) {
            if (current->customer.balance >= amount) {
                current->customer.balance -= amount;
                pushTransaction(&(current->customer), -amount);
                printf("Amount withdrawn successfully!\n");
            } else {
                printf("Insufficient funds.\n");
            }
            return;
        }
        current = current->next;
    }
    printf("Account not found.\n");
}

float viewBalance(Node* accounts, int accountNumber) {
    Node* current = accounts;
    while (current != NULL) {
        if (current->customer.accountNumber == accountNumber) {
            return current->customer.balance;
        }
        current = current->next;
    }
    return -1;
}

void viewTransactionHistory(Node* accounts, int accountNumber) {
    Node* current = accounts;
    while (current != NULL) {
        if (current->customer.accountNumber == accountNumber) {
            printf("\nTransaction History for Account %d:\n", accountNumber);
            Transaction* temp = current->customer.transactionHistory->top;
            while (temp != NULL) {
                printf("%s $%.2f\n", (temp->amount < 0) ? "Withdraw: " : "Deposit: ", (temp->amount < 0) ? -temp->amount : temp->amount);
                temp = temp->next;
            }
            return;
        }
        current = current->next;
    }
    printf("Account not found.\n");
}

void pushTransaction(Customer* customer, float amount) {
    Transaction* newTransaction = (Transaction*)malloc(sizeof(Transaction));
    if (newTransaction != NULL) {
        newTransaction->amount = amount;
        newTransaction->next = customer->transactionHistory->top;
        customer->transactionHistory->top = newTransaction;
    } else {
        printf("Error allocating memory for transaction.\n");
    }
}

void sortAccounts(Node* accounts) {
    Node* current;
    int swapped;
    char tempName[50];

    if (accounts == NULL) {
        printf("No accounts to display.\n");
        return;
    }

    do {
        swapped = 0;
        current = accounts;

        while (current->next != NULL) {
            if (strcmp(current->customer.name, current->next->customer.name) > 0) {
                strcpy(tempName, current->customer.name);
                strcpy(current->customer.name, current->next->customer.name);
                strcpy(current->next->customer.name, tempName);
                swapped = 1;
            }

            current = current->next;
        }
    } while (swapped);

    printf("\nAll Customer Accounts (Sorted by Name):\n");
    current = accounts;
    while (current != NULL) {
        printf("Name: %s, Account Number: %d, Balance: $%.2f\n", current->customer.name,
               current->customer.accountNumber, current->customer.balance);
        current = current->next;
    }
}

Node* searchAccountByNumber(Node* accounts, int accountNumber) {
    Node* current = accounts;
    while (current != NULL) {
        if (current->customer.accountNumber == accountNumber) {
            return current;
        }
        current = current->next;
    }
    return NULL; 
}

void deleteAccount(Node** accounts, int accountNumber) {
    Node* current = *accounts;
    Node* prev = NULL;

    while (current != NULL && current->customer.accountNumber != accountNumber) {
        prev = current;
        current = current->next;
    }

    if (current != NULL) {
        if (prev == NULL) {
            *accounts = current->next;
        } else {
            prev->next = current->next;
        }

        free(current->customer.transactionHistory);
        free(current);

        printf("Account deleted successfully!\n");
    } else {
        printf("Account not found.\n");
    }
}

int validateInput(const char *input) {
    int i = 0;
    int j = 0;

    while (input[i] != '\0') {
        if (!(isalpha(input[i]) || isspace(input[i]))) {
            printf("Please enter only alphabets and spaces.\n");
            return 1; 
        }
        i++;
    }

    j = 0;
    while (input[j] != '\0') {
        if (isspace(input[j])) {
            j++;
        } else {
            break; 
        }
    }

    if (j == i) {
        printf("Please enter spaces with alphabets.\n");
        return 1;
    } else {
        return 0;
    }
}

int validatePositiveInteger(const char *input, int *result) {
    int number = atoi(input);

    if (number > 0) {
        *result = number; 
        return 0; 
    } else {
        printf("Enter a positive integer number.\n");
        return 1;
    }
}

int validatePositiveFloat(const char *input, float *result) {
    float number = atof(input);

    if (number > 0) {
        *result = number;
        return 0;
    } else {
        printf("Enter a positive float number.\n");
        return 1;
    }
}
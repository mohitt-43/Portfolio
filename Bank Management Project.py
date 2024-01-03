import random

# Initialize dictionaries to store account information
accounts = {}
transactions = {}

def create_file(file_path):
    try:
        # Attempt to open the file in read mode
        with open(file_path, 'r'):
            pass
    except FileNotFoundError:
        # The file does not exist, create it
        with open(file_path, 'w'):
            pass

def write_nested_dict_to_file(file_path, nested_dict):
    with open(file_path, 'w') as file:
        for key, inner_dict in nested_dict.items():
            file.write(f"{key}:\n")
            for inner_key, value in inner_dict.items():
                file.write(f"  {inner_key}: {value}\n")

def read_nested_dict_from_file(file_path, nested_dict):
    current_key = None

    with open(file_path, 'r') as file:
        lines = file.readlines()

    for line in lines:
        line = line.strip()
        if line.endswith(':'):
            current_key = line[:-1]
            nested_dict[current_key] = {}
        elif current_key:
            inner_key, value = map(str.strip, line.split(':'))
            nested_dict[current_key][inner_key] = value

    return nested_dict

def write_list_nested_to_file(file_path, list_nested_dict):
    with open(file_path, 'w') as file:
        for key, inner_list in list_nested_dict.items():
            file.write(f"{key}:\n")
            for item in inner_list:
                file.write(f"  - {item}\n")

def read_list_nested_from_file(file_path, list_nested_dict):
    current_key = None

    with open(file_path, 'r') as file:
        lines = file.readlines()

    for line in lines:
        line = line.strip()
        if line.endswith(':'):
            current_key = line[:-1]
            list_nested_dict[current_key] = []
        elif current_key and line.startswith('- '):
            item = line[2:]
            list_nested_dict[current_key].append(item)

    return list_nested_dict

def signup():
    account = input("Enter account number: ")
    if account in accounts:
        username = input("Enter Username: ")
        password = input("Enter password: ")
        accounts[account]['username'] = username
        accounts[account]['password'] = password
        print("user created")
    else:
        print("no such account found")
        
def login():
    username = input("Enter your username: ")
    password = input("Enter your password: ")
    if username == 'admin' and password == 'admin':
        print("welcome admin")
        manager_interface()
    else:
        for account in accounts.items():
            if 'username' in account[1]:
                if account[1]['username'] == username and account[1]['password'] == password:
                    print("login successful")
                    print(f"welcome {account[1]['name']}")
                    customer_interface(account[0])
                    return()
        print("invalid username or password")

def manager_interface():
    """Manager interface."""
    while True:
        print("\nManager Interface")
        print("1. Create Account")
        print("2. View All Accounts (Sorted by Name)")
        print("3. Search Account by Account Number")
        print("4. Delete Account")
        print("5. Exit")

        choice = input("Enter your choice (1-5): ")

        if choice == '1':
            create_account()

        elif choice == '2':
            view_all_accounts()

        elif choice == '3':
            search_account()

        elif choice == '4':
            delete_account()

        elif choice == '5':
            print("Exiting manager interface. Thank you!")
            break

        else:
            print("Invalid choice. Please enter a number between 1 and 5.")

def customer_interface(account_no):
    """Customer interface."""
    while True:
        print("\nCustomer Interface")
        print("1. Deposit Money")
        print("2. Withdraw Money")
        print("3. View Balance (with Transaction Graph)")
        print("4. View Transaction History")
        print("5. Exit")

        choice = input("Enter your choice (1-5): ")

        if choice == '1':
            deposit(account_no)

        elif choice == '2':
            withdraw(account_no)

        elif choice == '3':
            view_balance_with_graph(account_no)
            
        elif choice == '4':
            view_transaction_history(account_no)

        elif choice == '5':
            print("Exiting customer interface. Thank you!")
            break

        else:
            print("Invalid choice. Please enter a number between 1 and 5.")

def create_account():
    """Create a new account."""
    account_number = str(random.randint(10000, 99999))
    name = get_alphabetic_input()
    balance = get_positive_number()
    accounts[account_number] = {'name': name, 'balance': balance}
    transactions[account_number] = []
    print(f"Account created successfully with account number {account_number}")
    
def is_alphabetic_with_space(input_string):
    """Check if the input contains only alphabets and spaces."""
    return all(char.isalpha() or char.isspace() for char in input_string)

def get_alphabetic_input():
    """Get input from the user, allowing only alphabets and spaces."""
    while True:
        user_input = input("Enter your name: ")
        if is_alphabetic_with_space(user_input):
            return user_input
        else:
            print("Error: Input should contain only alphabets and spaces.")
            
def get_positive_number():
    """Get a positive number from the user."""
    while True:
        user_input = input("Enter amount: ")
        try:
            number = float(user_input)
            if number > 0:
                return number
            else:
                print("Error: Please enter a positive number.")
        except ValueError:
            print("Error: Invalid input. Please enter a valid positive number.")

def view_all_accounts():
    """View all accounts without sorting."""
    if not accounts:
        print("No accounts found.")
        return

    print("All Accounts (Unsorted):")
    for account_number, account_info in accounts.items():
        print(f"Account Number: {account_number}, Name: {account_info['name']}, Balance: {account_info['balance']}")


def search_account():
    """Search for an account by account number."""
    account_number = input("Enter account number to search: ")
    if account_number in accounts:
        account_info = accounts[account_number]
        print(f"Account Number: {account_number}, Name: {account_info['name']}, Balance: {account_info['balance']}")
    else:
        print(f"Account with number {account_number} not found.")

def delete_account():
    """Delete an account."""
    account_number = input("Enter account number to delete: ")
    if account_number in accounts:
        del accounts[account_number]
        del transactions[account_number]
        print(f"Account with number {account_number} deleted successfully.")
    else:
        print(f"Account with number {account_number} not found.")

def deposit(account_number):
    """Deposit money into an account."""
    if account_number in accounts:
        amount = get_positive_number()
        accounts[account_number]['balance'] += amount
        transactions[account_number].append(f"Deposit: +{amount}")
        print(f"Deposit of {amount} successful. New balance: {accounts[account_number]['balance']}")
    else:
        print(f"Account with number {account_number} not found.")

def withdraw(account_number):
    """Withdraw money from an account."""
    if account_number in accounts:
        amount = get_positive_number()
        if accounts[account_number]['balance'] >= amount:
            accounts[account_number]['balance'] -= amount
            transactions[account_number].append(f"Withdrawal: -{amount}")
            print(f"Withdrawal of {amount} successful. New balance: {accounts[account_number]['balance']}")
        else:
            print("Insufficient funds.")
    else:
        print(f"Account with number {account_number} not found.")

def view_balance_with_graph(account_number):
    if account_number in accounts:
        balance = accounts[account_number]['balance']


        print(f"Account balance for account number {account_number}: {balance}")


    else:
        print(f"Account with number {account_number} not found.")

def view_transaction_history(account_number):
    """View the transaction history of an account."""
    if account_number in transactions:
        print(f"Transaction History for account number {account_number}:")
        for transaction in transactions[account_number]:
            print(transaction)
    else:
        print(f"Account with number {account_number} not found.")


# Main program loop

create_file('account.txt')
create_file('transactions.txt')

read_nested_dict_from_file('account.txt', accounts)
read_list_nested_from_file('transactions.txt', transactions)

while True:
    print("\nBank Management System")
    print("1. Signup")
    print("2. Login")
    print("3. Exit")

    choice = input("Enter your choice (1-3): ")

    if choice == '1':
        signup()

    elif choice == '2':
        login()
    elif choice == '3':
        print("Exiting program. Thank you!")
        break

    else:
        print("Invalid choice. Please enter a number between 1 and 3.")
        
write_nested_dict_to_file('account.txt',accounts)
write_list_nested_to_file('transactions.txt',transactions)
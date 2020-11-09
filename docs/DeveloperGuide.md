---
layout: page
title: Developer Guide
---

## Table of Contents
- [1. Introduction](#1-introduction)
- [2. Setting Up](#2-setting-up)
- [3. Design](#3-design)
    * [3.1. Architecture](#31-architecture)
    * [3.2. Components](#32-components)
        * [3.2.1 UI component](#321-ui-component)
        * [3.2.2 Logic component](#322-logic-component)
        * [3.2.3 Model component](#323-model-component)
        * [3.2.4 State component](#324-state-component)
        * [3.2.5 Storage component](#325-storage-component)
    * [3.3. Commons classes](#33-commons-classes)
- [4. Implementation](#4-implementation)
    * [4.1. State](#41-state)
    * [4.2. Parsers](#42-parsers)
        * [4.2.1. Page parsers](#421-page-parsers)
        * [4.2.2. Command parsers](#422-command-parsers)
        * [4.2.3. Interaction between parsers](#423-interaction-between-parsers)
    * [4.3. Commands](#43-commands)
        * [4.3.1. Add commands](#431-add-commands)
            * [4.3.1.1. Create budget](#4311-create-budget)
            * [4.3.1.2. Add expenditure](#4312-add-expenditure)
        * [4.3.2. Delete commands](#432-delete-commands)
            * [4.3.2.1. Delete budget](#4321-delete-budget)
            * [4.3.2.2. Delete expenditure](#4322-delete-expenditure)
        * [4.3.3. Edit Commands](#433-edit-commands)
            * [4.3.3.1. Edit budget](#4331-edit-budget)
            * [4.3.3.2. Edit expenditure](#4332-edit-expenditure)
        * [4.3.4. Sort commands](#434-sort-commands)
        * [4.3.5. Find & list commands](#435-find--list-commands)
        * [4.3.6. Undo & redo commands](#436-undo--redo-commands)
        * [4.3.7. Help command](#437-help-command)
    * [4.4. UI](#44-ui)
        * [4.4.1. List view rendering](#441-list-view-rendering)
        * [4.4.2. Dynamic updating](#442-dynamic-updating)
            * [4.4.2.1. Description](#4421-description)
            * [4.4.2.2. Implementation](#4422-implementation)
    * [5. Guides](#5-guides)
        * [5.1. Documentation](#51-documentation)
        * [5.2. Testing](#52-testing)
        * [5.3. Logging](#53-logging)
        * [5.4. Configuration](#54-configuration)
        * [5.5. DevOps](#55-devops)
    * [Appendix](#appendix)
        * [Product scope](#product-scope)
        * [User stories](#user-stories)
        * [Use cases](#use-cases)
        * [Non-functional requirements](#non-functional-requirements)
        * [Glossary](#glossary)
        * [Instructions for manual testing](#instructions-for-manual-testing)
        * [Effort](#effort)

## 1. Introduction

NUSave is a desktop application built for **university students who stay on campus** to help them organise, track and
manage their budgets.

It aims to alleviate the hassle that comes along with managing multiple budgets on different documents by providing a
one-stop solution. With NUSave, you can create, edit and delete budgets or expenditures,
as well as generate statistics based on your entries to gain useful insights regarding your spending
habits.

What's more, NUSave has:
- a Command Line Interface (CLI) catered to those who can type fast and prefer to use a keyboard. In other words, you
navigate the application and execute instructions by keying in text-based commands into the command box provided.
- a Graphical User Interface (GUI) to provide you with a visually appealing, aesthetic and intuitive user experience.

### Purpose

This document describes both the design and architecture of NUSave. It aims to serve as a guide for developers,
testers, and designers who are interested in working on NUSave.

## 2. Setting Up
(Contributed by Yu Ming)

Refer to the guide [_Setting up and getting started_](SettingUp.md).

## 3. Design

(Contributed by Song Yu)

This section elaborates on the high-level design of NUSave.

### 3.1. Architecture

(Contributed by Chin Hui)

This section elaborates on the high-level components of NUSave.

![Architecture Diagram](images/ArchitectureDiagram.png)

Figure 3.1.1. Architecture diagram of NUSave.

Figure 3.1.1 shows the relationship between the high-level components in NUSave.

Given below is a quick overview of each component:

The `Main` component has two classes called `Main` and `MainApp`.
It has two primary responsibilities:
* At launch: Initializes the components in the correct sequence and connects them up with each other.
* At shut down: Shuts down the components and invokes cleanup methods where necessary.

The [`Commons`](#33-commons-classes) component represents a collection of classes used by other components.

The rest of the application consists of five components:

1. [**`UI`**](#321-ui-component): Handles the UI of the application.
1. [**`Logic`**](#322-logic-component): Executes the commands.
1. [**`Model`**](#323-model-component): Holds the data of the application in memory.
1. [**`State`**](#324-state-componenet): Remembers the current state of the application.
1. [**`Storage`**](#325-storage-component): Reads data from, and writes data to, the hard disk.

For each of the five components:

* Its API is defined in an `interface` with the same name as the Component.
* Its functionality is exposed using a concrete `{Component Name} Manager` class which implements the corresponding API
`interface` mentioned in the previous point.
    - For example, the `Logic` component (see the class diagram below) defines its API in the `Logic.java` interface and exposes its functionality
    using the `LogicManager.java` class which implements the `Logic` interface.

![Architecture Sequence Diagram](images/ArchitectureSequenceDiagram.png)

Figure 3.1.2. Sequence diagram of the delete command.

Figure 3.1.2 shows how the components interact with each other for the scenario where the user issues the command `delete 1` in a budget page.


### 3.2. Components

This section elaborates on the different high-level components of NUSave.

#### 3.2.1. UI component

(Contributed by Song Yu)

![Structure of the UI Component](images/UiClassDiagram.png)

Figure 3.2.1.1. Architecture diagram of the `UI` component.

**API**: `Ui.java`

The UI consists of a `MainWindow` that is made up of various UI parts e.g.`CommandBox`, `ResultDisplay`,
`StatusBarFooter` etc. All of these classes inherit from the abstract `UiPart` class.

The `UI` component uses the JavaFX UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the `MainWindow` is specified in `MainWindow.fxml`.

In order to dynamically render data to be displayed to the user, when `setUpGuiComponents()` in `MainWindow` is called, 
the method `setStateBinders()` sets observer objects to observe changes in `State`. For a complete explanation,
refer to [4.4.2. Dynamic Updating](#442-dynamic-updating).

In summary, the `UI` component:

* Executes user commands using the `Logic` component.
* Listens for changes to `Model` and `State` data.

#### 3.2.2. Logic component
(Contributed by Yu Ming)

![Structure of the Logic Component](images/LogicClassDiagram.png)

Figure 3.2.2.1. Architecture diagram of the `Logic` component.

**API**: `Logic.java`

`Logic` uses the `MainPageParser` and `BudgetPageParser` classes to parse user commands. This results in a
`Command` object which is executed by the `LogicManager`. The command execution can affect the `Model`
(e.g. adding an expenditure). The result of the command execution is encapsulated as a `CommandResult` object
which is passed back to the `Ui`. In addition, the `CommandResult` object can also instruct the `Ui` to perform
certain actions, such as displaying help to the user.

`MainPageParser`:
- Parses all the commands that is entered by the user when the state of the NUSave is on `MAIN`.
- Includes commands such as `CreateBudgetCommand` and `OpenBudgetCommand` that are unique to the main page.

`BudgetPageParser`:
- Parses all the commands that is inputted by the user when the state of the NUSave is on `BUDGET`.
- Includes commands such as `AddExpendtureCommand` and `CloseBudgetCommand` that are unique to the budget page.

`Commands`:
-  The `Logic` component includes all commands that are executable on both the main and budget page. For a complete
elaboration on what each command does, refer to [4.3. Commands](#43-commands).

![Interactions Inside the Logic Component for the `delete 1` Command](diagrams/commandsPlantUML/diagram/DeleteBudgetCommand.png)

Figure 3.2.2.1. Sequence Diagram for the command: `delete 1`.

Figure 3.2.2.1 above represents the interactions within the `Logic` component for the `delete 1` command to delete a budget in NUSave.

#### 3.2.3. Model component
(Contributed by Chin Hui)

![Structure of the Model Component](images/ModelClassDiagram.png)

Figure 3.4.1: Architecture diagram of the `Model` component.

**API** : `Model.java`

<br/>

The `Model`:

* Stores a `UserPref` object that represents the user’s preferences.
* Stores a `Nusave` object that encapsulates `Budget` and `Expenditure` data.
* Exposes an unmodifiable `FilteredList<Renderable>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* Does not depend on any of the other three components.
* `FilteredList` was used in favor of `ObservableList` to facilitate the find command implementation.
The list can be filtered based on a `Predicate`, allowing for more flexibility for other 
filtering extensions i.e. filter by number of expenditures.

The `Nusave`:

* Implements methods that interact with `Budget` and `Expenditure`.
* Stores an `ObservableList<Renderable>` that is passed up to populate the `FilteredList<Renderable>`.
* Stores a `BudgetList` (wrapper class for a `List<Budget>`) to access `Expenditure` within a `Budget` since
`Expenditure` cannot be accessed through `ObservableList<Renderable>`.

The `Budget`:
* Implements the `Renderable` interface and can thus be stored in `FilteredList<Renderable>`.
* Contains a `Name`, `Date`, `Optional<Threshold>` and a `List<Expenditure>`.

The `Expenditure`:
* Implements the `Renderable` interface and can thus be stored in the `FilteredList<Renderable>`.
* Contains a `Name`, `Date`, `Price` and `Set<Tag>`.

#### 3.2.4. State component
 (Contributed by Song Yu)
 
 **API**: `State.java`
 
  ![Structure of the storage component](images/StateClassDiagram.png)
  
  Figure 3.5.1. Architecture diagram of the `State` component.
  
 The `State` component:
 * Represents the page NUSave is currently on. Specifically, it represents whether the user is looking at 
 the main page view or budget page view. The cuurent page is represented by an attribute in `StateManager`.
 * Stores data related to the current state of NUSave. This refers to data such as the page NUSave is currently on
 or the current budget that it is accessing.
 
 `State`  lives inside `Model`, where `Model` will use `State` to store stateful data. This data will be used to 
 update information displayed on the GUI, such as the current expenditure of the accessed budget, or list of expenditures 
 belonging to the accessed budget.
 
#### 3.2.5. Storage component
(Contributed by Wen Hao)

**API** : `Storage.java`

The `Storage` component:
* Saves `UserPref` objects in JSON format and reads it back.
* Saves all NUSave data in JSON format and reads it back.

![Structure of the storage component](diagrams/StorageClassDiagram.png)

Figure 3.6.1. Structure of the storage component.

The `Storage` component uses the Jackson API to convert Plain Old Java Objects (POJOs) into JSON files which are then
stored locally. It uses the same API to read existing JSON files during the launch of the application to load the stored
data into `NUSave`. As seen in Figure 3.6.1, `JsonUserPrefsStorage` is responsible for the reading and writing of
`UserPref` objects and contains the file path of its JSON file while `JsonNusaveStorage` is responsible for the
reading and writing of all NUSave data and contains the file path of its JSON file.

![Structure of the data stored by NUSave](diagrams/PojoClassDiagram.png)

Figure 3.6.2. Structure of the data stored by NUSave.

In order for them to be recognised by the Jackson API, NUSave data objects (such as `Budget` and `Expenditure`) must be
converted into POJOs. Figure 3.6.2 depicts how the respective POJO classes for each of the data objects interact with
one another.

### 3.3. Commons classes

(Contributed by Chin Hui)

Classes used by multiple components are in the `seedu.addressbook.commons` package, these include exceptions, error messages and classes with static methods that can be used by all components without instantiation.

## 4. Implementation

(Contributed by Song Yu)

This section elaborates on the implementations of various commands and components in NUSave.

### 4.1. State

(Contributed by Song Yu)

This section elaborates on how `State` affects how commands are parsed by parsers in NUSave.

`State` stores what page NUSave is currently on. If NUSave is on the main page, `State` stores an attribute of
`Page.MAIN`. If NUSave is on the budget page, `State` stores an attribute of `Page.BUDGET`.

The `Logic` component in NUSave relies on this data stored in `State` to decide which parser in NUSave will take
control of the execution of commands.

![Delete Command Activity Diagram](images/DeleteCommandActivityDiagram.png)

Figure 4.1.1. Activity Diagram of the command: `delete 1`.

To elaborate further, using Figure 4.1.1. as a reference, when the user executes a delete command, `delete 1`,
while on the main page:

1. `Logic` executes the command, checking whether the current page is a budget page or main page, depending on the 
current page (represented by `currentPage` attribute in `StateManager`) of NUSave.
2. Since NUSave is currently on `Page.MAIN`, `MainPageParser` takes control of the execution, parsing the command input by the user.
3. If the command syntax is valid, the delete command is parsed.

    3a. If the syntax is invalid, a `ParseException` is thrown.

4. NUSave deletes the budget based on the index specified by the user, i.e. the 1st budget displayed.

### 4.2. Parsers

(Contributed by Wen Hao)

This section elaborates on the details surrounding the parsers which are responsible for converting user inputs into `Command` objects.

All user inputs are parsed by two types of parsers:
1. Page Parsers
2. Command Parsers

These parsers are part of the `Logic` component as seen from the class diagram in Figure 4.2.1 below:

![Parser Class Diagram](diagrams/ParserClassDiagram.png)

Figure 4.2.1. Class diagram of parsers.

**Design Considerations**

* Option A: Use a single parser to parse both main and budget page commands
   * Pros: Less code to write.
   * Cons: Parser class will messy as it needs to differentiate between main and budget page commands that use the same command word.

* **Option B (Chosen):** Use page parsers to parse commands that are available on a page
   * Pros: Code is more organised and readable.
   * Cons: More code to write.

#### 4.2.1. Page parsers

(Contributed by Wen Hao)

Page parsers are responsible for determining the type of `Command` object that will be generated from a user input.
They enable NUSave to only recognise a set of commands specific to a certain `Page` when the user is on that `Page`.

They implement the `PageParser` interface.

All user inputs are first parsed by a page parser.
It identifies the first word to be the command word and uses it to determine the type of `Command` object that will be generated.
The remaining words are then passed as arguments to the respective command parsers.
A `ParseException` is thrown if the command word is not recognised by the page parser.

There are two types of page parsers:
1. `MainPageParser`
2. `BudgetPageParser`

User inputs are parsed by the `MainPageParser` if they are entered while NUSave is on the main page.
User inputs are parsed by the `BudgetPageParser` if they are entered while NUSave is on a budget page.

More information regarding what page the user is on can be found [here](#41-state).

#### 4.2.2. Command parsers

(Contributed by Wen Hao)

Command parsers are responsible for generating the different types of `Command` object.

They implement the `Parser<T>` interface.

Arguments in user inputs are parsed by a command parser to generate the respective `Command` object.
The type of `Command` object generated by a command parser follows the generic type of the `Parser<T>` it implements.
For example, `AddExpenditureCommandParser` implements the `Parser<AddExpenditureCommand>` interface. Hence, it will only generate `AddExpenditureCommand` objects.
A `ParseException` is thrown if the necessary arguments to generate the respective `Command` object are invalid or missing.

#### 4.2.3. Interaction between parsers

(Contributed by Wen Hao)

The interaction between the parsers is illustrated by the example usage seen in Figure 4.2.3.1 below:

![Parser Sequence Diagram](diagrams/ParserSequenceDiagram.png) 

Figure 4.2.3.1. Sequence diagram of the command: `delete `.

### 4.3. Commands

(Contributed by Song Yu)

This section elaborates on the implementations of the commands available in NUSave.

#### 4.3.1. Add commands

This section describes the details surrounding events at which users would wish to add information into NUSave. 
Specifically, when a user wishes to create a new budget to the Main Page, or when a user wishes to add an expenditure
to a budget.

##### 4.3.1.1. Create budget

(Contributed by Yu Ming)

This section elaborates on the `CreateBudgetCommand`.

The following activity diagram shows the events that occur when the user executes the `CreateBudgetCommand`.

![CreateBudgetCommand Activity Diagram](diagrams/commandsPlantUML/diagram/CreateBudgetCommandActivity.png) 

Figure 4.3.1.1: Activity diagram of the `CreateBudgetCommand`.

The command occurs in the `Main Page` of NUSave and results in the specified budget being created in
NUSave. This command therefore requires a compulsory name to specify the name of the budget to be created.

The following sequence diagram shows the interaction between the `Logic` component and `Model` component of NUSave 
depicting a scenario when the user wants to create a budget for his Temasek Hall Basketball CCA by entering the command
`create n/Temasek Hall Basketball p/100`.

![CreateBudgetCommand Sequence Diagram](diagrams/commandsPlantUML/diagram/CreateBudgetCommand.png) 

Figure 4.3.1.2: Sequence diagram of the `CreateBudgetCommand`.

<br/>

> Lifelines with a destroy marker (X) should end at the destroy marker (X) but due to a limitation of PlantUML, 
the lifeline reaches the end of diagram.

1. The `LogicManager` uses the `MainPageParser` to parse the given user input.
2. The `MainPageParser` will identify the command given by the user and pass the user input down to the
`CreateBudgetCommandParser`.
3. The `CreateBudgetCommandParser` will create a `Budget` with the given parameters **name** and **threshold** from the
user input.
4. The `CreateBudgetCommandParser` will then create a `CreateBudgetCommand` object with the created `Budget` object as
the input parameter.
5. The `CreateBudgetCommandParser` will then return a `CreateBudgetCommand` object.
6. `LogicManager` will now call the `execute` method in the `CreateBudgetCommand` object.
7. The `CreateBudgetCommand` will now call the `addBudget` method of the existing `Model` object and add the `Budget`
object created into NUSave.
8. The `CreateBudgetCommand` then returns a `CommandResult` indicating the successful addition of the `Budget` object.

With the above sequence, a budget will be successfully created by the user in his NUSave application and will be
reflected on the user interface.

##### 4.3.1.2. Add expenditure

(Contributed by David)

This section elaborates on the `AddExpenditureCommand`.

The following activity diagram shows the events that occur when the user executes the `AddExpenditureCommand`.

![AddExpenditureCommand Activity Diagram](diagrams/commandsPlantUML/diagram/AddExpenditureActivityDiagram.png) 

Figure 4.3.1.2.1: Activity diagram of the `AddExpenditureCommand`.

Similar to creating a budget, the add expenditure command also shows the interaction between the `Logic`
and `Model` components of NUSave. The sequence diagram depicts a scenario when the user wants to add an expenditure for 
his budget by entering the command `add n/Basketball p/20 t/Ball`.

![AddExpenditureCommand Sequence Diagram](diagrams/commandsPlantUML/diagram/AddExpenditureCommand.png) 

Figure 4.3.1.2.2: Sequence diagram of the `AddExpenditureCommand`.

> Lifelines with a destroy marker (X) should end at the destroy marker (X) but due to a limitation of PlantUML, 
the lifeline reaches the end of diagram.

1. Beginning with the `LogicManager`, the `LogicManager` hands the given user input to the `BudgetPageParser` 
to be parsed.
2. The `BudgetPageParser` will identify the command given by the user and passes the user input down to the
`AddExpenditureCommandParser` to be parsed.
3. The `AddExpenditureCommandParser` will create an `Expenditure` with the given parameters **name**, **price** and
optionally **tags** from the user input.
4. The `AddExpenditureCommandParser` will then create an `AddExpenditureCommand` object with the created `Budget` 
object as the input parameter.
5. The `AddExpenditureCommandParser` will then return an `AddExpenditureCommand` object.
6. `LogicManager` will now call the `execute` method in the `AddExpenditureCommand` object.
7. The `AddExpenditureCommand` will now call the `addExpenditure` method of the existing `Model` object and add the 
`Expenditure` object created into NUSave.
8. The `AddExpenditureCommand` then returns a `CommandResult` indicating the successful addition of the `Expenditure`
object.

With the above sequence, an expenditure will be successfully created by the user in his NUSave application under 
the specific budget and it will be immediately reflected on the user interface.

#### 4.3.2. Delete commands

(Contributed by David)

This section describes the details surrounding events at which users would wish to delete information from NUSave. 
Specifically, deletion can happen in two areas; when a user wishes to delete a budget from the Main Page, or when a 
user wishes to delete an expenditure from a budget.

##### 4.3.2.1. Delete budget

(Contributed by David)

This section elaborates on the `DeleteBudgetCommand`.

The following activity diagram to shows the events that occur when the user executes the `Delete Budget Command`.

![AddExpenditureCommand Activity Diagram](diagrams/commandsPlantUML/diagram/DeleteBudgetActivityDiagram.png) 

Figure 4.3.2.1.1: Activity diagram of the `DeleteBudgetCommand`.

The following command occurs in the `Main Page` of NUSave, and results in the specified budget of the particular index 
to be removed from NUSave. This command therefore requires a compulsory index to specify the particular budget to be 
removed.

Only when the index given by the user is valid (within the range of existing budgets), does the command execute 
successfully.

The following sequence diagram shows the interactions between the `Logic` and `Model` components of NUSave,
depicting a scenario where the user would like to delete the first budget on his list.

![DeleteBudgetCommand Sequence Diagram](diagrams/commandsPlantUML/diagram/DeleteBudgetCommand.png) 

Figure 4.3.2.1.2: Sequence diagram of the `DeleteBudgetCommand`.

> Lifelines with a destroy marker (X) should end at the destroy marker (X) but due to a limitation of PlantUML, 
the lifeline reaches the end of diagram.

1. The `LogicManager` uses the `MainPageParser` to parse the given user input.
2. The `MainPageParser` will identify the command given by the user and pass the user input down to the
`DeleteBudgetCommandParser`.
3. The `DeleteBudgetCommandParser` will create a `BudgetIndex` with the given parameters **index**  from the
user input.
4. The `DeleteBudgetCommandParser` will then create a `DeleteBudgetCommand` object with the created `BudgetIndex`
object as the input parameter.
5. The `DeleteBudgetCommandParser` will then return a `DeleteBudgetCommand` object back to the `LogicManager`.
6. `LogicManager` will now call the `execute` method in the `DeleteBudgetCommand` object, with the `Model` as a 
parameter.
7. The `DeleteBudgetCommand`'s `execute` method will now call the `deleteBudget` method of the existing `Model` object
passed in and delete the `Budget` object within NUSave.
8. The `DeleteBudgetCommand` then returns a `CommandResult` indicating the successful deletion of the `Budget` object.

With the above sequence, a budget will be successfully deleted by the user in his NUSave application, and it will be
reflected on the user interface through the successful `CommandResult` and updated budget list.

##### 4.3.2.2. Delete expenditure

(Contributed by David)

This section elaborates on the `DeleteExpenditureCommand`.

The following activity diagram to shows the events that occur when the user executes the `Delete Expenditure Command`.

![AddExpenditureCommand Activity Diagram](diagrams/commandsPlantUML/diagram/DeleteExpenditureActivityDiagram.png) 

Figure 4.3.2.2.1: Activity diagram of the `DeleteExpenditureCommand`.

The following command occurs in the `Budget Page` of NUSave, and results in the specified expenditure of the particular 
index to be removed from NUSave. This command therefore requires a compulsory index to specify the particular 
expenditure to be removed.

Only when the index given by the user is valid (within the range of existing budgets), does the command execute 
successfully.

The following sequence diagram is similar to Figure 4.3.2.1.1 which shows the interactions between the `Logic` and 
`Model` components of NUSave, depicting a scenario where the user within a budget would like to delete the first 
expenditure on his list.

![DeleteExpenditureCommand Sequence Diagram](diagrams/commandsPlantUML/diagram/DeleteExpenditureCommand.png)

Figure 4.3.2.2.2: Sequence diagram of the `DeleteExpenditureCommand`.

> Lifelines with a destroy marker (X) should end at the destroy marker (X) but due to a limitation of PlantUML, 
the lifeline reaches the end of diagram.

1. The `LogicManager` uses the `BudgetPageParser` to parse the given user input.
2. The `BudgetPageParser` will identify the command given by the user and create a `DeleteBudgetCommandParser`.
3. The `BudgetPageParser` will pass the user input into the newly created`DeleteBudgetCommandParser`.
3. The `DeleteExpenditureCommandParser` will create a `ExpenditureIndex` with the given parameters **index**  from the
user input.
4. The `DeleteExpenditureCommandParser` will then create a `DeleteExpenditureCommand` object with the created 
`ExpenditureIndex` object as the input parameter.
5. The `DeleteExpenditureCommandParser` will then return the `DeleteExpenditureCommand` object back to the 
`LogicManager`.
6. `LogicManager` will now call the `execute` method in the `DeleteExpenditureCommand` object, with the `Model` as a 
parameter.
7. The `DeleteExpenditureCommand`'s `execute` method will now call the `deleteExpenditure` method of the existing 
`Model` object passed in and delete the `Expenditure` object within NUSave.
8. The `DeleteExpenditureCommand` then returns a `CommandResult` indicating the successful deletion of the 
`Expenditure` object.

With the above sequence, a budget will be successfully deleted by the user in his NUSave application, and it will be
reflected on the user interface through the successful `CommandResult` and updated budget list.

#### 4.3.3. Edit Commands

(Contributed by David)

This section elaborates on the details surrounding events at which users would wish to edit information from NUSave. 
Specifically, editing can happen in two areas; when a user wishes to edit a budget from the `Main Page`, or when a 
user wishes to edit an expenditure from a budget within the `Budget Page`.

##### 4.3.3.1. Edit budget

(Contributed by Yu Ming)

This section elaborates on the `EditBudgetCommand`.

The following activity diagram to shows the events that occur when the user executes the Edit Budget Command.

![EditBudgetCommand Activity Diagram](diagrams/commandsPlantUML/diagram/EditBudgetCommandActivity.png) 

Figure 4.3.3.1.1: Activity diagram of the `EditBudgetCommand`.

The following command occurs in the `Main Page` of NUSave, and  results in the specified budget of the particular index 
to be edited within NUSave. As such, this command requires a compulsory index to specify the particular budget, 
along with fields at which the user would like to edit.

Only when the index is valid (within the range of existing budgets), and the user provides at least one field to 
be edited, does the command execute successfully.

The following sequence diagram shows the interactions between the `Logic` and `Model` components of NUSave,
depicting a scenario where the user would like to edit the first budget on his/her list, and change the `NAME` and
`THRESHOLD` of the budget to `Temasek Hall Basketball` and `1000` accordingly.

![EditBudgetCommand Sequence Diagram](diagrams/commandsPlantUML/diagram/EditBudgetCommand.png)

Figure 4.3.3.1.2: Sequence diagram of the `EditBudgetCommand`.

> Lifelines with a destroy marker (X) should end at the destroy marker (X) but due to a limitation of PlantUML, 
the lifeline reaches the end of diagram.

1. Beginning with the `LogicManager`, the `LogicManager` hands the given user input to the `MainPageParser` 
to be parsed.
2. The `MainPageParser` will identify the command given by the user and create an `EditBudgetCommandParser`.
3. The `MainPageParser` will pass the user input into the newly created `EditBudgetCommandParser`.
4. The `EditBudgetCommandParser` will create an `BudgetIndex` with the given parameters `index` from the
user input.
5. The `EditBudgetCommandParser` will then create an `EditBudgetDescriptor` with the given parameters of 
`name` and `threshold`.
6. The `EditBudgetCommandParser` will then create an `EditBugetCommand` with the `BudgetIndex` and 
`EditBudgetDescriptor`.
7. The `EditBudgetCommandParser` will then return the `EditBudgetCommand` object back to the `LogicManager`.
8. `LogicManager` will now call the `execute` method in the `EditBudgetCommand` object, with the `Model` as a 
parameter.
7. The `EditBudgetCommand`'s `execute` method will now call the `editBudget` method of the existing 
`Model` object passed in and update the `Budget` with a new edited `Budget` object within NUSave.
8. The `EditBudgetCommand` then returns a `CommandResult` indicating the successful editing of the 
`Budget`.

With the above sequence, a budget will be successfully edited by the user in his NUSave application, and it will 
be reflected on the user interface through the successful `CommandResult` and updated budget list.

##### 4.3.3.2. Edit expenditure

(Contributed by David)

This section elaborates on the `EditExpenditureCommand`.

The following activity diagram to shows the events that occur when the user executes the `Edit Expenditure Command`.

![AddExpenditureCommand Activity Diagram](diagrams/commandsPlantUML/diagram/EditExpenditureActivityDiagram.png) 

Figure 4.3.2.2.1: Activity Diagram of the `EditExpenditureCommand`.

The following command results in the specified expenditure of the particular index to be edited within the 
`Budget Page`. As such, this command requires a compulsory index to specify the particular expenditure, along with a
field at which the user would like to edit (`NAME`, `PRICE`, `TAG`).

Only when the index is valid (within the range of existing expenditures), and the user provides at least one field to 
be edited, does the command execute successfully.

The following sequence diagram shows the interactions between the `Logic` and `Model` components of NUSave,
depicting a scenario where the user would like to edit the first expenditure on his/her list, and change the previous 
`NAME`, `PRICE` and `TAG` to `Basketball`, `50` and `Ball` accordingly.

![EditExpenditureCommand Sequence Diagram](diagrams/commandsPlantUML/diagram/EditExpenditureCommand.png)

Figure 4.3.3.2.1: Sequence diagram of the `EditExpenditureCommand`.
> Lifelines with a destroy marker (X) should end at the destroy marker (X) but due to a limitation of PlantUML, 
the lifeline reaches the end of diagram.

1. Beginning with the `LogicManager`, the `LogicManager` hands the given user input to the `BudgetPageParser` 
to be parsed.
2. The `BudgetPageParser` will identify the command given by the user and create an `EditExpenditureCommandParser`.
3. The `BudgetPageParser` will pass the user input into the newly created `EditExpenditureCommandParser`.
4. The `EditExpenditureCommandParser` will create an `ExpenditureIndex` with the given parameters **index**  from the
user input.
5. The `EditExpenditureCommandParser` will then create an `EditExpenditureDescriptor` with the given parameters of 
**name**, **price** and **tags**.
6. The `EditExpenditureCommandParser` will then create an `EditExpenditureCommand` with the `Expenditure Index` and 
`EditExpenditureDescriptor`.
7. The `EditExpenditureCommandParser` will then return the `EditExpenditureCommand` object back to the `LogicManager`.
8. `LogicManager` will now call the `execute` method in the `EditExpenditureCommand` object, with the `Model` as a 
parameter.
7. The `EditExpenditureCommand`'s `execute` method will now call the `editExpenditure` method of the existing 
`Model` object passed in and update the `Expenditure` with a new `Expenditure` object within NUSave.
8. The `EditExpenditureCommand` then returns a `CommandResult` indicating the successful editing of the 
`Expenditure`.

With the above sequence, an expenditure will be successfully edited by the user in his NUSave application, and it will 
be reflected on the user interface through the successful `CommandResult` and updated budget list.

#### 4.3.4. Sort commands

(Contributed by Yu Ming)

This section elaborates on the events surrounding the sorting of budgets and expenditures.

The following activity diagram to shows the events that occur when the user executes the `SortBudgetCommand`.

![SortBudgetCommand Activity Diagram](diagrams/commandsPlantUML/diagram/SortBudgetCommandActivity.png) 

Figure 4.3.4.1: Activity diagram of the `SortBudgetCommand`.

The following command can occur either in the `Main Page` or `Budget Page` of NUSave, and results in either the budgets
or the expenditures to be sorted by name or created date. As such, this command requires a compulsory `SortType` field
to specify the particular type of sorting required.

Only when the `SortType` is valid (`NAME` or `TIME`) does the command execute successfully.

The following sequence diagram shows the interactions between the `Logic` and `Model` components of NUSave,
depicting a scenario where the user would like to sort the budgets by thier name in alphabetical order. 

![SortBudgetCommand Sequence Diagram](diagrams/commandsPlantUML/diagram/SortBudgetCommand.png)

Figure 4.3.4.2: Sequence diagram of the `SortBudgetCommand`.

> Lifelines with a destroy marker (X) should end at the destroy marker (X) but due to a limitation of PlantUML, 
the lifeline reaches the end of diagram.

1. Beginning with the `LogicManager`, the `LogicManager` hands the given user input to the `MainPageParser` 
to be parsed.
2. The `MainPageParser` will identify the command given by the user and create an `SortBudgetCommandParser`.
3. The `MainPageParser` will pass the user input into the newly created `SortBudgetCommandParser`.
4. The `SortBudgetCommandParser` will then create an `SortBugetCommand` with the sort type `NAME`
5. The `SortBudgetCommandParser` will then return the `SortBudgetCommand` object back to the `LogicManager`.
6. `LogicManager` will now call the `execute` method in the `SortBudgetCommand` object, with the `Model` as a 
parameter.
7. The `SortBudgetCommand`'s `execute` method will now call the `sortBudgetsByName` method of the existing 
`Model` and this will sort the list of budgets in NUSave according with the given sort type `NAME`.
8. The `SortBudgetCommand` then returns a `CommandResult` indicating the successful sorting of the 
`Budget` in NUSave by their name in alphabetical oredr.

With the above sequence, budgets will be successfully sorted by the user in his NUSave application by name in
alphabetical order, and it will be reflected on the user interface through the successful `CommandResult`
and updated budget list.

Note that the `sort` command can be executed on `Budget Page` view as well to sort the list of expenditures of a given
budget. The following sequence diagram shows the interactions between the `Logic` and `Model` components of NUSave,
depicting a scenario where the user would like to sort the expenditures by their created date with the lastest created
expenditure at the top of list of expenditures.

![SortExpenditureCommand Sequence Diagram](diagrams/commandsPlantUML/diagram/SortExpenditureCommand.png)

Figure 4.3.4.3: Sequence diagram of the `SortExpenditureCommand`

>Lifelines with a destroy marker (X) should end at the destroy marker (X) but due to a limitation of PlantUML, 
the lifeline reaches the end of diagram.

The details of the flow of `SortExpenditureCommand` will not be elaborate in details as it is similiar to
`SortBudgetCommand`.

#### 4.3.5. Find & list commands

This section elaborates on the events surrounding the find and list commands.

##### 4.3.5.1 List budget

(Contributed by Chin Hui)

The following sequence diagram shows the interactions between the `Logic` and `Model` components of NUSave,
depicting a scenario where the user would like to list all budgets.

![ListBudgetCommand Sequence Diagram](diagrams/commandsPlantUML/diagram/ListBudgetCommand.png)

Figure 4.3.5.1.1: Sequence diagram of the `ListBudgetCommand`.
>Lifelines with a destroy marker (X) should end at the destroy marker (X) but due to a limitation of PlantUML, 
the lifeline reaches the end of diagram.

1. The `LogicManager` uses the `MainPageParser` to parse the give user input.
2. The `MainPageParser` will identify the command given by the user and create a `ListBudgetCommand`.
3. The `MainPageParser` will then return the `ListBudgetCommand` object back to the `LogicManager`.
4. `LogicManager` will now call the `execute` method in the `ListBudgetCommand` object.
5. The `ListBudgetCommand` `execute` method will now call the `listBudgets` method of the existing `Model` object and
list all existing budgets within NUSave.
6. The `ListBudgetCommand` then returns a `CommandResult` indicating the successful listing of all budgets.

With the above sequence, all budgets will be listed by the user in his NUSave application and it will be reflected
on the user interface.

##### 4.3.5.2 Find budget
(Contributed by Chin Hui)

The following sequence diagram shows the interactions between the `Logic` and `Model` components of NUSave,
depicting a scenario where the user would like to find budgets by a search term/phrase.

![FindBudgetCommand Sequence Diagram](diagrams/commandsPlantUML/diagram/FindBudgetCommand.png)

Figure 4.3.5.2.1: Sequence diagram of the `FindBudgetCommand`.

> Lifelines with a destroy marker (X) should end at the destroy marker (X) but due to a limitation of PlantUML, 
the lifeline reaches the end of diagram.

1. The `LogicManager` uses the `MainPageParser` to parse the give user input.
2. The `MainPageParser` will identify the command given by the user and pass the user input down to the 
`FindBudgetCommandParser`.
3. The `FindBudgetCommandParser` will then create a `FindBudgetCommand` with the user input as the search term.
4. `FindBudgetCommandParser` then returns the `FindBudgetCommand` object back to the `LogicManager`.
5. The `LogicManager` will now call the `execute` method in the `FindBudgetCommand` object.
6. The `FindBudgetCommand` `execute` method will now call the `findBudget` method of the existing `Model` object and
apply a filter for budgets displayed by NUSave.
7. The `FindBudgetCommand` then returns a `CommandResult` indicating that all budgets containing the search term
has been displayed.

With the above sequence, all budgets containing the search term entered will be filtered 
and displayed on the user interface.

##### 4.3.5.3 List expenditure

(Contributed by Chin Hui)

The following sequence diagram shows the interactions between the `Logic` and `Model` components of NUSave,
depicting a scenario where the user would like to list all expenditure within the current budget.

![ListBudgetCommand Sequence Diagram](diagrams/commandsPlantUML/diagram/ListExpenditureCommand.png)

Figure 4.3.5.3.1.: Sequence diagram of the `ListExpenditureCommand`.

>Lifelines with a destroy marker (X) should end at the destroy marker (X) but due to a limitation of PlantUML, 
the lifeline reaches the end of diagram.

1. The `LogicManager` uses the `BudgetPageParser` to parse the give user input.
2. The `BudgetPageParser` will identify the command given by the user and create a `ListExpenditureCommand`.
3. The `BudgetPageParser` will then return the `ListExpenditureCommand` object back to the `LogicManager`.
4. `LogicManager` will now call the `execute` method in the `ListExpenditureCommand` object.
5. The `ListExpenditureCommand` `execute` method will now call the `listExpenditure` method of the existing `Model` object and
list all existing expenditures within the current budget.
6. The `ListExpenditureCommand` then returns a `CommandResult` indicating the successful listing of all expenditures.

With the above sequence, all expenditures will be listed by the user in his NUSave application, and it will be reflected
on the user interface.

##### 4.3.5.4 Find expenditure

(Contributed by Chin Hui)

The following sequence diagram shows the interactions between the `Logic` and `Model` components of NUSave,
depicting a scenario where the user would like to find expenditures in a budget by a search term/phrase.

![FindBudgetCommand Sequence Diagram](diagrams/commandsPlantUML/diagram/FindExpenditureCommand.png)

Figure 4.3.5.4.1.: Sequence diagram of the `FindExpenditureCommand`.

>Lifelines with a destroy marker (X) should end at the destroy marker (X) but due to a limitation of PlantUML, 
the lifeline reaches the end of diagram.

1. The `LogicManager` uses the `BudgetPageParser` to parse the give user input.
2. The `BudgetPageParser` will identify the command given by the user and pass the user input down to the 
`FindExpenditureCommandParser`.
3. The `FindExpenditureCommandParser` will then create a `FindExpenditureCommand` with the user input as the search term.
4. `FindExpenditureCommandParser` then returns the `FindExpenditureCommand` object back to the `LogicManager`.
5. The `LogicManager` will now call the `execute` method in the `FindExpenditureCommand` object.
6. The `FindExpenditureCommand` `execute` method will now call the `findExpenditures` method of the existing `Model` object and
apply a filter for expenditures displayed by NUSave.
7. The `FindExpenditureCommand` then returns a `CommandResult` indicating that all expenditures in the current budget
 containing the search term has been displayed.

With the above sequence, all expenditures containing the search term entered will be filtered 
and displayed on the user interface.

#### 4.3.6. Undo & redo commands
(Contributed by Wen Hao)

This section elaborates on the events of the undo and redo commands.

The undo and redo commands are implemented using the following classes:

| Class             | Details            | Purpose |
| -------- | --------------------------- | -------------- |
| `VersionedNusave` | Contains a `BudgetList` and `BudgetIndex` | Represents the data and view of NUSave at a certain point in time |
| `Node<T>`         | Contains a value of type `T`, next `Node<T>` and previous `Node<T>` | Represents a doubly linked list |
| `HistoryManager<T>` | Contains a pointer to a `Node<T>` | Represents an iterator to iterate through a doubly linked list represented by `Node<T>` |

The following class diagram shows how the classes interact with each other:

![Undo redo class diagram](diagrams/UndoRedoClassDiagram.png)

Figure 4.3.6.1. Class diagram of the classes related to undo and redo command.

The pointer in `HistoryManager<VersionedNusave>` is always pointing to the latest `VersionedNusave` in a doubly linked list represented by `Node<T>`. If an undo command is executed, it will load the previous `VersionedNusave` and move the pointer backward. If a redo command is executed, it will load the next `VersionedNusave` and move the pointer forward. Whenever the user makes changes to NUSave data, a `VersionedNusave` is instantiated with a deep copy of the `BudgetList` from `Nusave` and the `BudgetIndex` from `State`. It replaces the next `Node<T>` (if any) of the `Node<T>` that `HistoryManager<VersionedNusave>` is currently pointing to before the pointer is moved forward.

To better illustrate the process, an example usage shown below:

Step 1: The user launches NUSave. `HistoryManager<VersionedNusave>` is instantiated with an empty doubly linked list.

![Undo redo object diagram for step 1](diagrams/UndoRedoState0.png)

Step 2: The user makes changes to NUSave data by creating a `Budget` named "demo". Before the change is made, a `VersionedNusave` is instantiated in case the user wants to undo. `HistoryManager<VersionedNusave>` adds this `VersionedNusave` to the doubly linked list.

![Undo redo object diagram for step 2](diagrams/UndoRedoState1.png)

Step 3: The user executes the undo command. Before reverting the changes, a `VersionedNusave` is instantiated in case the user wants to redo. `HistoryManager<VersionedNusave>` adds this `VersionedNusave` to the doubly linked list before moving its pointer backward to retrieve the previous `VersionedNusave`. The `BudgetList` from the previous `VersionedNusave` is loaded into `Nusave` while the `BudgetIndex` from the previous `VersionedNusave` is used to set `State`. Once this is done, the GUI should reflect that the "demo" budget is removed from NUSave.

![Undo redo object diagram for step 3](diagrams/UndoRedoState2.png)

Step 4: The user executes the redo command. `HistoryManager<VersionedNusave>` retrieves the next `VersionedNusave` from the pointer and moves its pointer forward. The `BudgetList` from the next `VersionedNusave` is loaded into `Nusave` while the `BudgetIndex` from the next `VersionedNusave` is used to set `State`. Once this is done, the GUI should reflect that the "demo" budget is added back to NUSave.

![Undo redo object diagram for step 4](diagrams/UndoRedoState3.png)

Step 5: The user makes changes to NUSave data by creating a `Budget` named "demo2". Before the change is made, a `VersionedNusave` is instantiated in case the user wants to undo. `HistoryManager<VersionedNusave>` adds this `VersionedNusave` to the doubly linked list before moving its pointer forward.

![Undo redo object diagram for step 5](diagrams/UndoRedoState4.png)

The following sequence diagram shows how the undo command is executed:

![Undo redo sequence diagram](diagrams/UndoSequenceDiagram.png)

Figure 4.3.6.2. Sequence diagram of the `UndoCommand`.

#### 4.3.7 Help command

(Contributed by Yu Ming)

This section explains the `Help Command`.

The following activity diagram to shows the events that occur when the user executes the Help Command.

![HelpCommand Activity Diagram](diagrams/commandsPlantUML/diagram/HelpCommandActivity.png) 

Figure 4.3.7.1: Activity diagram of the `HelpCommand`.

The following command can occur either in the `Main Page` or `Budget Page` of NUSave, and the help notes will be
displayed in the result box of the UI Component.

The following sequence diagram shows the interactions between the `Logic` and `Model` components of NUSave,
depicting a scenario where the user would like to ask for help to be displayed. 

![HelpCommand Sequence Diagram](diagrams/commandsPlantUML/diagram/HelpCommand.png)

Figure 4.3.7.1.2: Sequence diagram of the `HelpCommand`.

>Lifelines with a destroy marker (X) should end at the destroy marker (X) but due to a limitation of PlantUML, 
the lifeline reaches the end of diagram.

1. Beginning with the `LogicManager`, the `LogicManager` hands the given user input to the `MainPageParser` 
to be parsed.
2. The `MainPageParser` will identify the command given by the user and create an `HelpBudgetCommand`.
3. The `MainPageParser` will then return the `HelpBudgetCommand` object back to the `LogicManager`.
4. `LogicManager` will now call the `execute` method in the `HelpBudgetCommand` object, with the `Model` as a 
parameter.
5. The `HelpBudgetCommand`'s `execute` method will return a `CommandResult` indicating the successful calling for the
help command, and the help information will be displayed in the result box in NUSave. 

With the above sequence, the help information will be successfully shown to the user in NUSave and it will be reflected 
on the user interface.

Note that the `help` command can be executed on `Budget Page` view as well, but it will display a different set of help
message that is unique to the `Budget Page` view with commands that can be executed on the view.

### 4.4. UI

This section elaborates on the implementations of various `Ui` features.

#### 4.4.1. List View Rendering

(Contributed by Wen Hao)

This section elaborates on how budget and expenditure cards are rendered within the List View UI component on the GUI of NUSave.

As there is a need to constantly re-render the contents within the List View to reflect user changes, we have adopted the **Observer Pattern**
so that data can be sent from the `Model` component to the `UI` component efficiently. Using the JavaFX API, the List View is binded to an `ObservableList`
such that any changes to the `ObservableList` will trigger an update within the List View accordingly.

![Class Diagram between `Model` and `UI`](diagrams/ListViewClassDiagram.png)

Figure 4.4.1.1. Class diagram to illustrate the observer pattern

The List View UI component is able to display both budget and expenditure cards interchangeably through the `Renderable` interface.
Both `Budget` and `Expenditure` classes implement the `Renderable` interface.
As such, `Budget` and `Expenditure` objects can be added to the `ObservableList` of `Renderable` which the List View is binded to.
Whenever changes are made to the `ObservableList`, the List View generates either a `BudgetCard` or `ExpenditureCard` depending on the runtime type of the `Renderable` object.

To facilitate sort and find commands, an additional layer of filtering is accomplished using the `FilteredList` class. Different types of predicates are
supplied to the filtered list depending on user needs.

**Design Considerations**

* Option A: Use separate List Views for `Budget` and `Expenditure`
   * Pros: Less prone to the error where both `BudgetCard` and `ExpenditureCard` are displayed simultaneously
   * Cons: Higher difficulty and more code to write as there is the need to handle the replacement of the entire List View

* **Option B (Chosen):** Use a single List View to hold both `Budget` and `Expenditure` with the use of a `Renderable` interface
   * Pros: Easier to extent as new classes just need to implement the `Renderable` interface to be displayed
   * Cons: More prone to the error where both `BudgetCard` and `ExpenditureCard` are displayed simultaneously

#### 4.4.2. Dynamic Updating

(Contributed by Song Yu)

This section talks about how data is dynamically updated on the GUI of NUSave.  

As an overview, we use the **Observer Pattern** to dynamically update these modified data. We force this communication by 
using a `StateBinder` interface, where `bind()` is called to bind all `StateBinders` to `StateManager`.

![Class Diagram between StateBinders and State](images/StateBinders_State_Class_Diagram.png)

Figure 4.4.2.1. Class diagram to illustrate the observer pattern.

1. On initialisation of NUSave, `MainWindow` calls `this.setStateBinders()`, which calls `StateBinderList.bindAll()`.
2. `StateBinderList` calls `bind()` on every `StateBinder`. 
3. `bind()` will connect to `isBudgetPageProp`, `infoBoxSecondRowProp` and `thresholdStringProp` in `State`.
4. Whenever the attributes in `State` as referenced in step 3 are updated, the `StateBinders` are notified, which in turn
updates the GUI.

As seen, there is also a combination of the [**Facade Pattern**](https://nus-cs2103-ay2021s1.github.io/website/se-book-adapted/chapters/designPatterns.html#facade-pattern)
and [**N-tier Architectural Style**](https://nus-cs2103-ay2021s1.github.io/website/se-book-adapted/chapters/architecture.html#n-tier-architectural-style) 
to link `StateBinder` and `StateManager` together.

##### 4.4.2.1. Description

When a page switches from the main page to a budget page, information in the `InfoBox` and `Title` UI classes are updated.
When the `StringProperty` and `BooleanProperty` attributes are updated in `State`, the observers in `InfoBox` and `Title`
are notified, which updates the data displayed.  

###### 4.4.2.2. Implementation

The change in information displayed occurs when the user inputs one of the following commands:
1. Opening a budget: `open`
2. Closing a budget: `close`
3. Adding an expenditure: `add`
4. Editing an expenditure: `edit`
5. Deleting an expenditure: `delete`

###### Sequence Diagram

The following sequence diagram shows the interactions between the `Ui`, `Logic`,`Model` and `State` components of NUSave,
depicting a scenario where the user opens a budget.

![Update Title Sequence Diagram](images/UpdateTitleSequenceDiagram.png)

Figure 4.4.2.2.1.1. Sequence diagram of the `OpenBudgetCommand`.

1. `MainWindow` is called with the String `open 1`.
2. `MainWindow` uses `LogicManager` to execute the given user input.
1. The `LogicManager` uses the `MainPageParser` to parse the given user input.
2. The `MainPageParser` identifies the command given by the user and creates an `OpenBudgetCommandParser`.
3. The `MainPageParser` passes the user input into the newly created`OpenBudgetCommandParser`.
4. The `OpenBudgetCommandParser` creates a `OpenBudgetCommand` object.
5. The `OpenBudgetCommandParser` returns the `OpenBudgetCommand` object back to `LogicManager`.
6. `LogicManager` calls the `execute` method in the `OpenBudgetCommand` object, with the `Model` as a 
parameter.
7. The `OpenBudgetCommand`'s `execute` method calls the `openBudget` method of the existing 
`Model` object passed in.
8. `ModelManager` calls its own `setOpenCommandState` method, which retrieves relevant data to update `State`.
9. `ModelManager` calls `State`'s `setOpenCommandState` method, updating state data relevant to opening a budget.
10. `State`'s `StringProperty` and `BooleanProperty` attributes are updated, which notifies `InfoBox`
and `Title` to update.

With the above sequence, a budget will successfully be opened, and the `Title` component reflects the name of 
the budget, while the `InfoBox` component reflects the total expenditure and threshold of the budget.

**Design Considerations**

* Option A: Use Model-View Controller (MVC) Pattern to update GUI
   * Pros: Good separation of concern, with controller being in charge of updating both the model and Ui.
   * Cons: Hard to implement as controllers will have to be set up from scratch.

* **Option B (Chosen):** Use Observer Pattern to update GUI
   * Pros: Able to use proprietary JavaFx library to implement, enforce loose coupling with Observer interface.
   * Cons: External code can easily invoke observer as `bind()` method is public. 

## 5. Guides

### 5.1. Documentation

* [Documentation guide](Documentation.md)

### 5.2. Testing

* [Testing guide](Testing.md)

### 5.3. Logging

* [Logging guide](Logging.md)

### 5.4. Configuration

* [Configuration guide](Configuration.md)

### 5.5. DevOps

* [Dev Ops guide](DevOps.md)

## Appendix

### Product Scope
(Contributed by Yu Ming and David)

**Target User Profile:**

* university students staying on campus
* has a need to manage a multiple budgets and expenditures
* prefers using desktop over other platforms
* types fast and prefers typing to mouse interactions

**Value Proposition:** 

* manages expenditures faster than a typical mouse/GUI driven application
* allows users to keep track of their budgets on a centralised platform

### User Stories

Priorities: 
* High (must have) - `* * *` 
* Medium (nice to have) - `* *`
* Low (unlikely to have) - `*`

| Priority | As a …​                                 | I want to …​                           | So that I can…​                                                     |
| -------- | ------------------------------------------ | ----------------------------------------- | ---------------------------------------------------------------------- |
| `* * *`  | new user                                   | see a list of available commands          | refer to instructions when I forget how to use the application         |
| `* * *`  | new user                                   | view sample data                          | have a better understanding of how the application works               |
| `* * *`  | new user                                   | clear my existing data                    | remove all the sample data from my application                         |
| `* * *`  | user                                       | create a budget                           | add a new budget that I need                                           |
| `* * *`  | user                                       | delete a budget                           | remove a budget that I no longer need                                  |
| `* * *`  | user                                       | edit a budget                             | edit the name or the threshold that needs to be changed                |
| `* *`    | user                                       | sort my budgets                           | view my budgets based on name or date                                  |
| `* *`    | user                                       | find a budget by search term              | locate a budget easily                                                 |
| `* *`    | user                                       | list out my budgets                       | view by budgets in the default manner after finding or sorting         |
| `* * *`  | user                                       | open a budget                             | view the expenditures of a budget                                      |
| `* * *`  | user                                       | close a budget                            | return to the main window                                              |
| `* *`    | user                                       | see how many expenditures I have in a budget | have a better understanding of the particular budget                |
| `* * *`  | user                                       | add an expenditure                        | add a new expenditure to a budget                                      |
| `* * *`  | user                                       | delete an expenditure                     | remove an expenditure that I no longer need                            |
| `* * *`  | user                                       | edit an expenditure                       | edit the name, price and tag that needs to be changed                  |
| `* *`    | user                                       | sort my expenditures                      | view my expenditures based on name or date                             |
| `* *`    | user                                       | find an expenditure by search term        | locate an expenditure easily                                           |
| `* *`    | user                                       | list out my expenditures                  | view by expenditures in the default manner after finding or sorting    |
| `* *`    | user                                       | see if I have passed my threshold         | manage my expenses better                                              |
| `* *`    | user                                       | add tags to my expenditure                | categorise my expenditures                                             |
| `* *`    | user                                       | exit NUSave                               | stop using the application                                             |

### Use Cases

#### Use Case: UC01 - Viewing the help menu
(Contributed by Yu Ming)

System: NUSave
<br/>Use Case: UC01 - Viewing the help menu
<br/>Actor: User
<br/>MSS:
1. User enters the command to show the help menu in NUSave.
2. NUSave displays the help menu to the user.
<br/> Use case ends.

**Extensions**
- 1a. NUSave detects an error in the entered command.
    - 1a1. NUSave shows an error message.
    - 1a2. User enters new command.
    <br/> Steps 1a1-1a2 are repeated until the command entered is correct.
    <br/> Use case resumes at step 2.

#### Use Case: UC02 - Creating a budget
(Contributed by Yu Ming)

System: NUSave
<br/>Use Case: UC02 - Creating a budget
<br/>Actor: User
<br/>Preconditions: User is on the main page
<br/>MSS:
1. User enters the command to add a new budget in NUSave.
2. NUSave adds the new budget and displays the updated list of budgets to the user.
<br/> Use case ends.

**Extensions**
- 1a. NUSave detects an error in the entered command (for example, an invalid threshold).
    - 1a1. NUSave shows an error message.
    - 1a2. User enters a new command.
    <br/> Steps 1a1-1a2 are repeated until the command entered is correct.
    <br/> Use case resumes at step 2.

#### Use Case: UC03 - Editing a budget
(Contributed by Yu Ming)

System: NUSave
<br/>Use Case: UC03 - Editing a budget
<br/>Actor: User
<br/>Preconditions: User is on the main page
<br/>MSS:
1. User enters the command to edit a budget in NUSave.
2. NUSave replaces the old budget with the newly edited budget and displays the updated list of budgets to the user.
<br/> Use case ends.

**Extensions**
- 1a. NUSave detects an error in the entered command.
    - 1a1. NUSave shows an error message.
    - 1a2. User enters new command.
    <br/> Steps 1a1-1a2 are repeated until the command entered is correct.
    <br/> Use case resumes at step 2.
- 1b. NUSave detects that the given budget does not exist in NUSave.
    - 1b1. NUSave shows an error message.
    - 1b2. User enters new command.
    <br/> Steps 1b1-1b2 are repeated until the command entered is correct.
    <br/> Use case resumes at step 2.
    

#### Use Case: UC04 - Deleting a budget
(Contributed by David)

System: NUSave
<br/>Use Case: UC04 - Deleting a budget
<br/>Actor: User
<br/>Preconditions: User is on the Main page, there must be an existing budget
<br/>MSS:
1. User enters the command to delete a budget.
2. NUSave deletes the budget and displays the updated list to the user.
<br/> Use case ends.

**Extensions**
- 1a. NUSave detects an error in the entered command.
    - 1a1. NUSave shows an error message.
    - 1a2. User enters new command.
    <br/> Steps 1a1-1a2 are repeated until the command entered is correct.
    <br/> Use case resumes at step 2.

#### Use Case: UC05 - Opening a budget

MSS (Contributed by Song Yu)

System: NUSave
<br/>Use Case: UC05 - Opening a budget
<br/>Actor: User
<br/>Preconditions: User is on the main page
<br/>MSS:
1. User enters the command to open a budget in NUSave.
2. NUSave opens the budget displays the list of expenditures belonging to that budget to the user.
<br/> Use case ends.

**Extensions**
- 1a. NUSave detects an error in the entered command.
    - 1a1. NUSave shows an error message.
    - 1a2. User enters new command.
    <br/> Steps 1a1-1a2 are repeated until the command entered is correct.
    <br/> Use case resumes at step 2.

#### Use Case: UC06 - Closing a budget

MSS (Contributed by Song Yu)

System: NUSave
<br/>Use Case: UC06 - Closing a budget
<br/>Actor: User
<br/>Preconditions: User is on the budget page
<br/>MSS:
1. User enters the command to close a budget in NUSave.
2. NUSave closes the budget and displays the list of budgets in NUSave.
<br/> Use case ends.

**Extensions**
- 1a. NUSave detects an error in the entered command.
    - 1a1. NUSave shows an error message.
    - 1a2. User enters new command.
    <br/> Steps 1a1-1a2 are repeated until the command entered is correct.
    <br/> Use case resumes at step 2.

#### Use Case: UC07 - Sorting budgets
(Contributed by Yu Ming)

System: NUSave
<br/>Use Case: UC07 - Sorting budgets
<br/>Actor: User
<br/>Preconditions: User is on the main page, NUSave contains at least 2 or more budgets
<br/>MSS:
1. User enters the command to sort budgets in NUSave.
2. NUSave sorts all budgets and displays the updated list of budgets to user.
<br/> Use case ends.

**Extensions**
- 1a. NUSave detects an error in the entered command.
    - 1a1. NUSave shows an error message.
    - 1a2. User enters new command.
    <br/> Steps 1a1-1a2 are repeated until the command entered is correct.
    <br/> Use case resumes at step 2.


#### Use Case: UC08 - Finding budgets

(Contributed by Chin Hui)

System: NUSave
<br>Use Case: UC08 - Finding budgets
<br>Actor: User
<br>Preconditions: User is on the main page.
<br>MSS:
1. User enters the command to find budgets in NUSave.
2. NUSave finds all budgets matching the user input and displays the updated list of budgets to user.
<br>Use case ends.

**Extensions**
- 1a. NUSave detects an error in the entered command.
    - 1a1. NUSave shows an error message.
    - 1a2. User enters new command.
    <br> Steps 1a1-1a2 are repeated until the command entered is correct.
    <br> Use case resumes at step 2.
- 2a. NUSave detects that no budgets were found.
    - 2a1. NUS displays an empty list view with a message noting that no budgets matched the user input.

#### Use Case: UC09 - Listing budgets

(Contributed by Chin Hui)

System: NUSave
<br/>Use Case: UC10 - Listing budgets
<br/>Actor: User
<br/>Preconditions: User is on the main page.
<br/>MSS:
1. User enters the command to list all budgets in NUSave.
2. NUSave lists all existing budgets in memory, displaying them in the list view and shows the success message.
<br>Use case ends.

#### Use Case: UC10 - Clearing budgets
(Contributed by Wen Hao)

System: NUSave
<br/>Use Case: UC10 - Clearing budgets
<br/>Actor: User
<br/>Preconditions: User is on the main page.
<br/>MSS:
1. User enters the command to clear all budgets in NUSave.
2. NUSave deletes all existing budgets, displays an empty list view and shows the success message.
<br/> Use case ends.

#### Use Case: UC11 - Adding an expenditure
(Contributed by Song Yu)

System: NUSave
<br/>Use Case: UC11 - Adding an expenditure
<br/>Actor: User
<br/>Preconditions: User is on the budget page
<br/>MSS:
1. User enters the command to add a new expenditure in NUSave.
2. NUSave adds the new expenditure and displays the updated list of expenditures to the user.
<br/> Use case ends.

**Extensions**
- 1a. NUSave detects an error in the entered command (for example, an invalid price).
    - 1a1. NUSave shows an error message.
    - 1a2. User enters new command.
    <br/> Steps 1a1-1a2 are repeated until the command entered is correct.
    <br/> Use case resumes at step 2.

#### Use Case: UC12 - Editing an expenditure
(Contributed by David)

System: NUSave
<br/>Use Case: UC12 - Editing an expenditure
<br/>Actor: User
<br/>Preconditions: User is on the Budget page, there must be an existing expenditure
<br/>MSS:
1. User requests to <ins>open a budget (UC05)</ins> to view the list of expenditures in the budget page view.
2. NUSave opens the budget and displays the list of expenditures belonging to that budget.
3. User enters the command to edit an expenditure within the budget.
4. NUSave edits the expenditure and displays the updated list to the user.
<br/> Use case ends.

**Extensions**
- 3a. NUSave detects an error in the entered command.
    - 3a1. NUSave shows an error message.
    - 3a2. User enters new command.
    <br/> Steps 3a1-3a2 are repeated until the command entered is correct.
    <br/> Use case resumes at step 4.

#### Use Case: UC13 - Deleting an expenditure
(Contributed by David)

System: NUSave
<br/>Use Case: UC13 - Deleting an expenditure
<br/>Actor: User
<br/>Preconditions: User is on the Budget page, there must be an existing expenditure
<br/>MSS:
1. User requests to <ins>open a budget (UC05)</ins> to view the list of expenditures in the budget page view.
2. NUSave opens the budget and displays the list of expenditures belonging to that budget.
3. User enters the command to delete an expenditure within the budget.
4. NUSave deletes the expenditure and displays the updated list to the user.
<br/> Use case ends.

**Extensions**
- 3a. NUSave detects an error in the entered command.
    - 3a1. NUSave shows an error message.
    - 3a2. User enters new command.
    <br/> Steps 3a1-3a2 are repeated until the command entered is correct.
    <br/> Use case resumes at step 4.

#### Use Case: UC14 - Sorting expenditures
(Contributed by Yu Ming)

System: NUSave
<br/>Use Case: UC14 - Sorting expenditures
<br/>Actor: User
<br/>Preconditions: User is on the budget page, the given budget contains at least 2 or more expenditures
<br/>MSS:
1. User requests to <ins>open a budget (UC05)</ins> to view the list of expenditures in the budget page view.
2. NUSave opens the budget and displays the list of expenditures belonging to that budget.
3. User enters the command to sort expenditures in NUSave.
4. NUSave sorts the expenditures and displays the updated list to the user.
<br/> Use case ends.

**Extensions**
- 3a. NUSave detects an error in the entered command.
    - 3a1. NUSave shows an error message.
    - 3a2. User enters new command.
    <br/> Steps 3a1-3a2 are repeated until the command entered is correct.
    <br/> Use case resumes at step 4.


#### Use Case: UC15 - Finding expenditures

(Contributed by Chin Hui)

System: NUSave
<br>Use Case: UC08 - Finding expenditures
<br>Actor: User
<br>Preconditions: User is on the budget page.
<br>MSS:
1. User enters the command to find expenditures in NUSave.
2. NUSave finds all expenditures matching the user input and displays the updated list of expenditures to user.
<br>Use case ends.

**Extensions**
- 1a. NUSave detects an error in the entered command.
    - 1a1. NUSave shows an error message.
    - 1a2. User enters new command.
    <br> Steps 1a1-1a2 are repeated until the command entered is correct.
    <br> Use case resumes at step 2.
- 2a. NUSave detects that no expenditures were found.
    - 2a1. NUS displays an empty list view with a message noting that no expenditures matched the user input.
    
#### Use Case: UC16 - Listing expenditures

(Contributed by Chin Hui)

System: NUSave
<br/>Use Case: UC10 - Listing expenditures
<br/>Actor: User
<br/>Preconditions: User is on the budget page.
<br/>MSS:
1. User enters the command to list all expenditures in NUSave.
2. NUSave lists all existing expenditures in the current budget in memory, displaying them in the list view and 
shows the success message.
<br>Use case ends.

#### Use Case: UC17 - Undoing an action

(Contributed by Wen Hao)

System: NUSave
<br/>Use Case: UC17 - Undoing an action
<br/>Actor: User
<br/>Preconditions: User has just launched NUSave that contains a budget named "demo".
<br/>MSS:
1. User <ins>deletes the "demo" budget (UC04)</ins>.
2. NUSave deletes the "demo" budget and removes it from the list view.
3. User enters the undo command.
4. NUSave loads the state before the previous command and adds the "demo" budget into the list view.
<br/> Use case ends.

**Extensions**
- 1a. User enters the undo command without deleting the "demo" budget.
    - 1a1. NUSave shows "no action to undo" error message.
    - 1a2. Use case resumes at step 1.

#### Use Case: UC18 - Redoing an action

(Contributed by Wen Hao)

System: NUSave
<br/>Use Case: UC18 - Redoing an action
<br/>Actor: User
<br/>Preconditions: User has just launched NUSave that contains a budget named "demo".
<br/>MSS:
1. User <ins>undoes the deletion of the "demo" budget (UC17)</ins>.
2. NUSave loads the state before using the delete command and removes the "demo" budget from the list view.
<br/> Use case ends.
3. User enters the redo command.
4. NUSave loads the state before using the undo command and adds the "demo" budget into the list view.
<br/> Use case ends.

**Extensions**
- 1a. User enters the redo command without undoing any actions.
    - 1a1. NUSave shows "no action to redo" error message.
    - 1a2. Use case resumes at step 1.

### Non-Functional requirements

(Contributed by Chin Hui)

1. NUSave should work on any _mainstream OS_ as long as it has Java `11` or above installed.
2. NUSave should be able to hold up to 1000 budgets and expenditures without a noticeable sluggishness in performance for typical usage.
3. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) 
should be able to accomplish most of the tasks at a faster speed compared to clicking with the mouse.
4. NUSave should serve only a single user at a time on a single client.
5. NUSave should not require an internet connection to run.
6. NUSave should have sufficient help messages such that a novice is able to learn to use the commands quickly.
7. NUSave should save its data locally.
8. NUSave should have proper error handling such that the application does not crash and the corresponding error
message is displayed to the user.
9. Features should be implemented such that they can undergo automated testing.
10. NUSave should have an intuitive User Interface such that a novice user should be able to understand
what the elements of the application represents.

### Glossary

(Contributed by Song Yu)

| Term | Explanation |
| ---- | ----------- |
| Expenditure                 | Refers to a single item to be recorded in NUSave.                                                                                                                              
| Budget                      | Refers to how NUSave stores related expenditures under one group. A budget can also hold additional information about this list of expenditures, such as the target limit of what is to be spent (i.e. threshold). 
| Main Page                   | Refers to the page that displays the list of budgets that is stored in NUSave.                                                                                                 
| Budget Page                 | Refers to the page that displays the list of expenditures belonging in a specific budget that is stored in NUSave.                                                             
| Threshold                   | Refers to the target limit that can be spent in that budget.
| PlantUML                    | A software tool used by NUSave's team to render UML diagrams in this developer guide. 
| NUS                         | Stands for National University of Singapore.
| API                         | Stands for 'Application Programming Interface', which abstracts away underlying implementation and only exposes objects or methods a developer needs.
| JSON                        | Stands for 'Javascript Standard Object Notation', which is a form of syntax used for storing data. 
| CLI                         | Stands for **Command Line Interface**. CLI-based Applications (i.e. NUSave) focuses on processing commands in the form of text entered from the keyboard.                       
| GUI                         | Stands for **Graphical User Interface**. GUIs work as the communication channel between the program and the user. Users interact with NUSave through the GUI, on their devices. 
| UML                         | Stands for 'Unified Modeling Diagram'. A general-purpose, standardized modeling language used in the field of software engineering.
| NFR                         | Stands for 'Non-functional Requirements', which specifies the constraints under which the system is developed and operated.
| Mainstream OS               | Stands for 'Mainstream Operating Systems', such as Windows, MacOS, Linux, Unix, OS-X.
| MSS                         | Stands for 'Main Success Scenario', which describes the interaction for a given use case, assuming nothing goes wrong.

### Instructions for Manual Testing

(Contributed by Chin Hui)

Given below are instructions to test the application manually. These instructions should be complemented
with the user guide for comprehensive testing. The state of the application is assumed to contain some data
either sample data from when the application is first launched or a customised data set.

#### Launch and shutdown
1. Launching the application

   1. Download the jar file and copy into an empty folder.

   2. Double-click the jar file.<br>
   Expected: Shows the GUI with a set of sample budgets. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   2. Re-launch the application by double-clicking the jar file.<br>
   Expected: The most recent window size and location is retained.

#### Add commands
1. Adding a budget

    1. Prerequisites: User is in the main page with multiple budgets in the list.
    
    2. Test case: `create n/Temasek Hall Basketball`<br>
       Expected: A budget by the name of Temasek Hall Basketball is created with its budget threshold set to $0.
       Details of the added budget is shown in the status message. The newly added budget is displayed as the first
       item in the GUI list.
       
    3. Test case: `create n/Temasek Hall Basketball p/1000`<br>
       Expected: A budget by the name of Temasek Hall Basketball is created with its budget threshold set to $1000.
       Details of the added budget is shown in the status message. The newly added budget is displayed as the first
       item in the GUI list.
    
    4. Test case: `create`<br>
       Expected: No budget will be created. You will get an error message stating that the command format is invalid,
       with details of the proper format accompanied by examples.
       
    5. Test case: `create n/`<br>
       Expected: No budget will be created. You will get an error message stating that the name should only contain
       alphanumeric characters and spaces, it should not be blank.
   
2. Adding an expenditure

    1. Prerequisites: User is in the budget page with multiple expenditures in the list.
    
    2. Test case: `add n/shirt p/15 t/clothing`<br>
       Expected: An expenditure with the name shirt, price of $15 and tag of clothing will be added into the current budget.
       Details of the added expenditure is shown in the status message. The newly added expenditure is displayed as the first
       item in the GUI list.
       
    3. Test case: `add n/shirt t/clothing`<br>
        Expected: No expenditure will be created. You will get an error message stating that the command format is invalid,
        with details of the proper format accompanied by examples.
        
    4. Test case: `add n/shirt p/15 t/some of my favourite shirts`<br>
        Expected: No expenditure will be created. You will get an error message stating that each tag is limited to 15
        characters long.
        
    5. Test cae: `add n/blue shirt p/15 t/clothing t/tops t/shopping t/blue`<br>
        Expected: No expenditure will be created. You will get an error message stating that each
        expenditure can only have a maximum of 3 tags.
        

#### Delete Commands
1. Deleting a budget

    1. Prerequisites: User is in the main page with multiple budgets in the list.

    2. Test case: `delete 1`<br>
      Expected: First budget is deleted from the list. Details of the deleted budget is shown in the status message.
    
    3. Test case: `delete 0`<br>
      Expected: No budget is deleted. You will get an error message stating that the index is out of range and the valid 
      range is from 1-100.
    
    4. Test case: `delete`<br>
      Expected: No budget is deleted. You will get an error message stating that the index should be an integer.
      
    5. Test case: `delete x`<br>
      Expected: No budget is deleted. You will get an error message stating that the index should be an integer.
      
2. Deleting an expenditure

    1. Prerequisites: User is in the budget page with multiple expenditures in the list.
    
    2. Test case: `delete 1`<br>
    Expected: First expenditure is deleted from the list. Details of the deleted expenditure is shown 
    in the status message.
    
    3. Test case: `delete 0`<br>
    Expected: No budget is deleted. You will get an error message stating that the index is out of range and the valid 
          range is from 1-100.
    
    4. Test case: `delete`<br>
    Expected: No budget is deleted. You will get an error message stating that the index should be an integer.
          
    5. Test case: `delete x`<br>
    Expected: No budget is deleted. You will get an error message stating that the index should be an integer.
      
#### Edit Commands

1. Editing a budget
    1. Prerequisites: User is in the main page with multiple budgets in the list.
    
    2. Test case: `edit 1 n/Daily Expenses p/500`<br>
    Expected: The first budget in the list will be edited to have the name 'Daily Expenses' and a budget threshold
    of $500. This is assuming the first budget in the list had a different name with a different budget threshold.
    
    3. Test case: `edit 1 n/Daily Expenses`<br>
    Expected: The first budget in the list will be edited to have the name 'Daily Expenses'
     while its budget threshold will remain the same. This is assuming the first budget in the list had a different name.
     
    4. Test case: `edit 1 p/500`<br>
    Expected: The first budget in the list will be edited to have a budget threshold of $500. This is assuming the
    first budget in the list had a different budget threshold.
    
    5. Test case: `edit 0 p/300`<br>
    Expected: No budget will be edited. You will get an error message stating that the index is out of range and the valid
    range is from 1-100.
    
    6. Test case: `edit 1 p/-10`<br>
    Expected: No budget will be edited. You will get an error message stating that thresholds cannot be $0 or less. The valid
    range of thresholds which is between $0.01 and $1,000,000 will be stated.
    
2. Editing an expenditure
    1. Prerequisites: User is in the budget page with multiple expenditures in the list.
    
    2. Test case: `edit 1 n/blue shirt p/15 t/clothing`<br>
    Expected: The first expenditure in the current budget will be edited to have the name 'blue shirt', a price of $15
    and a clothing tag. This is assuming that the first expenditure in the current budget had a different name, price 
    and the tags clothing, shirt and shopping (`tags` will be overwritten).
    
    3. Test case: `edit 1 n/blue shirt`<br>
    Expected: The first expenditure in the current budget will be edited to have the name `blue shirt`.
    This is assuming that the first expenditure in the current budget had a different name.
    
    4. Test case: `edit 1 p/15`<br>
    Expected: The first expenditure in the current budget will be edited to have the price of $15.
    This is assuming that the first expenditure in the current budget had a different price.
    
    5. Test case: `edit 0 n/blue shirt`<br>
    Expected: No expenditure will be edited. You will get an error message stating that the index is out of range and the valid
    range is from 1-100.
    
    6. Test case: `edit 1 p/-10`<br>
    Expected: No expenditure will be edited. You will get an error message stating that prices cannot be $0 or less. The valid
    range of prices which is between $0.01 and $10,000 will be stated.
    
#### Sort Commands

1. Sorting budgets

    1. Prerequisite: User is in the main page with multiple budgets in the list.
    
    2. Test case: `sort name`<br>
    Expected: Current budgets in the GUI list will be sorted by name in alphabetical order.
    
    3. Test case: `sort time`<br>
    Expected: Current budgets in the GUI list will be sorted by creation date, with the most recently created at the top.
    If two budgets are created on the same day they will be further sorted by name in alphabetical order.
    
    4. Test case: `sort test`<br>
    Expected: Current budgets will not be sorted. You will get an error message stating that the sort type is not supported.
   
2. Sorting expenditures
    
    1. Prerequisite: User is in the budget page with multiple expenditures in the list.
    
    2. Test case: `sort name`<br>
    Expected: Current expenditures in the GUI list will be sorted by name in alphabetical order.
    
    3. Test case: `sort time`<br>
    Expected: Current expenditures in the GUI list will be sorted by creation date, with the most recently created at the top.
    If two expenditures are created on the same day they will be further sorted by name in alphabetical order.
    
    4. Test case: `sort test`<br>
    Expected: Current expenditures will not be sorted. You will get an error message stating that the sort type is not supported.

#### Find Commands
1. Finding Budgets

    1. Prerequisite: User is in the main page with multiple budgets in the list.
    
    2. Test case: `find NUS`<br>
    Expected: Budgets with names that contain the search term 'NUS' will be displayed in the GUI list.
    
    3. Test case: `find`<br>
    Expected: You will get an error message stating that the search term should not be blank.

2. Finding Expenditures

    1. Prerequisite: User is in the budget page with multiple expenditures in the list.

    2. Test case: `find shirt`<br>
    Expected: Expenditures in the current budget with names that contain the search term 'shirt' will be displayed in the GUI list.
    
    3. Test case: `find`<br>
    Expected: You will get an error message stating that the search term should not be blank.

#### List Commands

1. Listing Budgets

    1. Prerequisite: User is in the main page with multiple budgets in the list. The `find` command was successfully used,
    the GUI list currently only displays budgets containing 'NUS'.
    
    2. Test case: `list`<br>
    Expected: All budgets are now displayed in the GUI list.
    
2. Listing Expenditures

    1. Prerequisite: User is in the budget page with multiple expenditures in the list. The `find` command was successfully used,
    the GUI list currently only displays expenditures containing `shirt`.
    
    2. Test caes: `list`<br>
    Expected: All expenditures are now displayed in the GUI list.

#### Universal Commands

1. Help Command

    1. Test case: `help`<br>
    Expected: Description, format and examples of all commands are displayed in the result box.
    
#### Opening a budget

1. Prerequisite: User is in the main page with multiple budgets in the list.

2. Test case: `open 1`<br>
Expected: Opens the first budget in the current GUI list of budgets. The GUI list now displays the expenditures within
the first budget.

3. Test case: `open -1`<br>
Expected: You will get an error message stating that the current index is out of bounds and the correct range which is
from 0-100.

#### Closing a budget

1. Prerequisite: User is in the budget page with multiple expenditures in the list.

2. Test case: `close`<br>
Expected: Closes the current budget and return back to the main page.

#### Redo Command

1. Test case: `redo` (assuming that the `undo` command was used previously to undo a `create` command)<br>
Expected: The `create` command that was undone will be called again. The budget that was created using the `create`
command will be reflected in the GUI list.

#### Undo Command

1. Test case: `undo` (assuming that `create` command was the most recently called command)<br>
Expected: The `create` command will be undone. The budget that was created will be removed from the GUI list as if the
`create` command was not called.

### Effort

(Contributed by all <3)

Throughout the development, our team shared the common goal of making NUSave an application that is intuitive, object-oriented 
and presentable. After devoting countless late nights both as a team and individually, we managed to pull through and create
a product that we are proud of. Amassing over 10,000 lines of code combined, we had to adhere to strict deadlines, cultivate 
a culture of open communication and proactively support one another. Aside from the weekly official meetings, we frequently 
met up to conduct code reviews and pair programming to ensure that everyone was on the same page. 

To enable navigation between pages, we had to restructure a large portion of AB3 so that NUSave incorporates the concept of
having states. As such, we implemented a new architectural `State` component to keep track of what page the user is on and reflect the respective page view.

To fulfil the functional requirements of NUSave, an additional layer of data was integrated. On top of the one to many relationship between `Nusave`
and `Budget`, `Budget` also has a one to many relationship with `Expenditure`.

To implement the undo and redo commands, we had to come up with our own version of a doubly-linked list, `Node<T>`, as well as
its own iterator, `HistoryManager<T>`. Furthermore, not only the data had to be cached, but also the state of NUSave in `VersionedNusave`.

Coding aside, we placed an equal emphasise on UI/UX.

Firstly, we revamped the GUI entirely. Taking into consideration colour consistency, design trends and ease of navigation,
we used Figma to generate a mock-up for our reference throughout the development. Despite the limitations of JavaFX and our knowledge in CSS, we were successful
in replicating our ideal design.

To make our application more dynamic, we researched on numerous APIs and narrowed down to using JavaFX's `Property` and `Bindings` interfaces.
Through countless trial and errors, we managed to render NUSave's UI components dynamically while adhering to OO principles.

Finally, much effort was put into ensuring that our documentation was organised and pleasant to read.
We had meetings to standardise every section in terms of language, structure and diagrams. After multiple iterations,
consistency was achieved in both our user and developer guides.

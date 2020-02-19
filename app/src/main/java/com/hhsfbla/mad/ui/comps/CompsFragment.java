package com.hhsfbla.mad.ui.comps;

import android.accounts.Account;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.adapters.CompsAdapter;
import com.hhsfbla.mad.data.CompType;
import com.hhsfbla.mad.data.Competition;

import java.util.ArrayList;
import java.util.List;

public class CompsFragment extends Fragment {

    private CompsViewModel mViewModel;
    private RecyclerView eventRecyclerView;
    private CompsAdapter adapter;
    private SearchView searchView;
    private List<Competition> comps;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private static final String TAG = "COMPS";


    public static final Competition[] competitions = {
            new Competition("3-D Animation", "Two (2) parts: a prejudged project and a presentation. Competitors must complete both parts for award eligibility.\n\nTopic: Using 3-D animation, create an informational video to train new FBLA chapter officers. The video should include:\n\nTeam building\nOfficer duties\nDeveloping a Program of Work\n\nSkills: This event provides recognition for FBLA members who possess knowledge of basic skills and procedures and the ability to make intelligent business decisions.", CompType.TECH, R.drawable.tech_icon),
            new Competition("Accounting 1", "60-minute test administered during the National Leadership Conference (NLC). Participants must not have had more than two (2) semesters or one (1) semester equivalent to a full year in block scheduling in high school accounting instruction.\n\nObjective Test Competencies: Journalizing; Account Classification; Terminology, Concepts, and Practices; Types of Ownership; Posting; Income Statement; Balance Sheet; Worksheet; Bank Reconciliation; Payroll; Depreciation; Manual and Computerized Systems; Ethics\n\nSkills: The accurate keeping of financial records is an ongoing activity in all types of businesses. This event provides recognition for FBLA members who have an understanding of and skill in basic accounting principles and procedures.", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Accounting 2", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: Financial Statements; Corporate Accounting; Ratios and Analysis; Accounts Receivable and Payable; Budgeting and Cash Flow; Cost Accounting/Manufacturing; Purchases and Sales; Journalizing and Posting; Income Tax; Payroll; Inventory; Plant Assets and Depreciation; Departmentalized Accounting; Ethics; Partnerships\n" +
                    "\n" +
                    "Skills: The accurate keeping of financial records is a vital ongoing activity in all types of businesses. This event provides recognition for FBLA members who have demonstrated an understanding of and skill in accounting principles and procedures as applied to sole proprietorships, partnerships, and corporations.", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Advertising", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: Personal Selling and Sales Promotion; Traditional and Alternative Advertising Media; Consumer Behavior; Basic Marketing Functions; Branding and Positioning; Economy; Advertising Plan; Legal and Ethical Issues; Diversity and Multicultural Market; Public Relations; Creation of the Advertisement; Consumer-Oriented Advertising; Financial Planning; Communication; Consumer Purchase Classifications; Target Market; Market Segmentation; Product Development; Product Life Cycle; Price Planning; Channels of Distribution; Marketing Research; Effective Advertising and Promotional Messages; Budget; Financing Advertising Campaigns; Demographics; History and Influences; Advertising Industry and Careers; Supply Chain Management; Distribution Logistics; Internet; Self-Regulation; Careers; Advertising Workplace; Leadership, Career Development, and Team Building; Risk Management\n" +
                    "\n" +
                    "Skills: This event provides recognition for FBLA members who possess knowledge of the basic principles of advertising.", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Agribusiness", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: Economics; Finance and Accounting; Health, Safety, and Environmental Management; Management Analysis and Decision Making; Marketing; Terminology and Trends\n" +
                    "\n" +
                    "Skills: This event provides recognition for FBLA members who demonstrate an understanding of and skill in basic agribusiness concepts and procedures.\n" +
                    "\n", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("American Enterprise Project", "Two (2) parts: a prejudged report and a presentation. Competitors must complete both parts for award eligibility.\n" +
                    "\n" +
                    "Skills: This event recognizes FBLA chapters that develop projects within the school and/or community that increase the understanding of and support for the American enterprise system by developing an informational/educational program. The project must promote an awareness of some facet of the American enterprise system within the school and/or community and be designed for chapter participation.", CompType.PROJECT, R.drawable.project_icon),
            new Competition("Banking and Financial Systems", "Two (2) parts: an objective test and interactive role play or presentation. A 60-minute objective test will be administered onsite at the NLC. Team competitors will take one (1) objective test collaboratively.\n" +
                    "\n" +
                    "Objective Test Competencies: Concepts and Practices; Basic Terminology; Government Regulation of Financial Services; Impact of Technology on Financial Services; Types and Differences of Various Institutions; Ethics; Careers in Financial Services; Taxation\n" +
                    "\n" +
                    "Case: A problem or scenario encountered in the banking or financial business community.\n" +
                    "\n" +
                    "Skills: Understanding how financial institutions operate is important to successful business ownership and management. It also is valuable for personal financial success. This event provides recognition for FBLA members who have an understanding of and skills in the general operations of various components of the financial services sector.", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Broadcast Journalism", "Includes a presentation. Review specific guidelines for each event as guidelines vary.\n" +
                    "\n" +
                    "Topic: You and/or your team are part of your school’s broadcast team. Create a live broadcast event that includes the following:\n" +
                    "\n" +
                    "Social media/cell phones on campus\n" +
                    "Financial literacy story for your audience\n" +
                    "Sports story from your campus\n" +
                    "Skills: Whether using the medium of TV, radio, or internet, the broadcast journalist has to look for possible news or feature stories that might be of interest to the public. This event provides recognition for FBLA members who demonstrate skill and understanding of the profession.", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Business Calculations", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: Consumer Credit; Mark-Up and Discounts; Data Analysis and Reporting; Payroll; Interest Rates; Investments; Taxes; Bank Records; Insurance; Ratios and Proportions; Depreciation; Inventory\n" +
                    "\n" +
                    "Skills: Acquiring a high level of mathematics skill to solve business problems is a challenge for all prospective business employees. This event provides recognition for FBLA members who have an understanding of mathematical functions in business applications.", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Business Communication", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: Nonverbal and Verbal Communication; Communication Concepts; Report Application; Grammar; Reading Comprehension; Editing and Proofreading; Word Definition and Usage; Capitalization and Punctuation; Spelling; Digital Communication\n" +
                    "\n" +
                    "Skills: Learning to communicate in a manner that is clearly understood by the receiver of the message is a major task of all businesspeople. This event provides recognition for FBLA members who work toward improving their business communication skills of writing, speaking, and listening.", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Business Ethics", "Includes a presentation or role play. Review specific guidelines for each event as guidelines vary.\n" +
                    "\n" +
                    "Topic: Research the ethical issues of photo manipulation related to journalistic practices and public opinion.\n" +
                    "\n" +
                    "Skills: Ethical decision making is essential in the business world and the workplace. This team event recognizes FBLA members who demonstrate the ability to present solutions to ethical situations encountered in the business world and the workplace.", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Business Financial Plan", "Two (2) parts: a prejudged report and a presentation. Competitors must complete both parts for award eligibility.\n" +
                    "\n" +
                    "Topic: Create a Business Financial Plan for a local rental business that will also do business online.  The business should be specifically targeted for your community.  The Business Financial Plan should include a name for the business, what items you will be renting, plans for needed construction and/or renovation to the building, equipment to be purchased, inventory for your launch date, hours of operation, staffing requirements, information on developing your e-business website, a promotional plan, and a social media plan.\n" +
                    "\n" +
                    "Skills: Business financial planning is paramount to the success of any business enterprise. This event is designed to recognize FBLA members who possess the knowledge and skills needed to establish and develop a complete financial plan for a business venture.", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Business Law", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: Legal Systems; Contracts and Sales; Business Organization; Property Laws; Agency and Employment Laws; Negotiable Instruments; Insurance Secured Transactions, Bankruptcy; Consumer Protection and Product/Personal Liability; Computer Law; Domestic and Private Law\n" +
                    "\n" +
                    "Skills: This event provides recognition for FBLA members who are familiar with specific legal areas that most commonly affect personal and business relationships.\n" +
                    "\n", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Business Plan", "Two (2) parts: a prejudged report and a presentation. Competitors must complete both parts for award eligibility.\n" +
                    "\n" +
                    "Skills: This event recognizes FBLA members who demonstrate an understanding and mastery of the process required to develop and implement a new business venture. The business venture must be currently viable and realistic and must not have been in operation for a period exceeding twelve months before the NLC.", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Client Service", "Includes a presentation or role play. Review specific guidelines for each event as guidelines vary.\n" +
                    "\n" +
                    "Skills: This event provides members with an opportunity to develop and demonstrate skill in interacting with internal and external clients to provide an outstanding client service experience. The client service consultant engages clients in conversation regarding products, handles inquiries, solves problems, and uncovers opportunities for additional assistance. Participants develop speaking ability and poise through participation as well as critical-thinking skills.", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Coding and Programming", "Includes a demonstration of the project. Review specific guidelines for each event as guidelines vary.\n" +
                    "\n" +
                    "Topic: Develop an original computer program to track hours for the Community Service Awards program for your chapter members. The program must complete a minimum of the following tasks:\n" +
                    "\n" +
                    "Track student name, student number, and grade in school with ability to enter/view/edit.\n" +
                    "Track the total of community service hours per student with ability to enter/view/edit.\n" +
                    "Track the Community Service Award program category per student with the ability to enter/view/edit.\n" +
                    "Generate or print weekly/monthly report to show total number of community service hours per student.\n" +
                    "Generate or print weekly/monthly report to show Community Service Award program categories and total hours.\n" +
                    "Data must be stored persistently. Storage may be in a relational database, a document-oriented NoSQL database, flat text files, flat JSON, or XML files.\n" +
                    "The user interface must be a GUI with a minimum of five different control types including such things as drop-down lists, text fields, check boxes, emails, or other relevant control types.\n" +
                    "All data entry must be validated with appropriate user notifications and error messages including the use of required fields.\n" +
                    "Skills: Certain types of processes require that each record in the file be processed. Coding & Programming focuses on these procedural style processing systems. This event tests the programmer’s skill in designing a useful, efficient, and effective program.", CompType.TECH, R.drawable.tech_icon),
            new Competition("Community Service Project", "Includes a demonstration of the project. Review specific guidelines for each event as guidelines vary.\n" +
                    "\n" +
                    "Topic: Develop an original computer program to track hours for the Community Service Awards program for your chapter members. The program must complete a minimum of the following tasks:\n" +
                    "\n" +
                    "Track student name, student number, and grade in school with ability to enter/view/edit.\n" +
                    "Track the total of community service hours per student with ability to enter/view/edit.\n" +
                    "Track the Community Service Award program category per student with the ability to enter/view/edit.\n" +
                    "Generate or print weekly/monthly report to show total number of community service hours per student.\n" +
                    "Generate or print weekly/monthly report to show Community Service Award program categories and total hours.\n" +
                    "Data must be stored persistently. Storage may be in a relational database, a document-oriented NoSQL database, flat text files, flat JSON, or XML files.\n" +
                    "The user interface must be a GUI with a minimum of five different control types including such things as drop-down lists, text fields, check boxes, emails, or other relevant control types.\n" +
                    "All data entry must be validated with appropriate user notifications and error messages including the use of required fields.\n" +
                    "Skills: Certain types of processes require that each record in the file be processed. Coding & Programming focuses on these procedural style processing systems. This event tests the programmer’s skill in designing a useful, efficient, and effective program.", CompType.PROJECT, R.drawable.project_icon),
            new Competition("Computer Applications", "Two (2) parts: a production test administered and proctored at a designated school-site prior to the NLC and a 60-minute objective test administered onsite at NLC. Competitors must complete both parts for award eligibility.\n" +
                    "\n" +
                    "Production Test Competencies: Create, Search, and Query Databases; Spreadsheet Functions and Formulas; Text Slide Graphics and Presentations; Business Graphics; Word Processing\n" +
                    "\n" +
                    "Objective Test Competencies: Basic Computer Terminology and Concepts; Presentation, Publishing, and Multimedia Applications; Email, Integrated and Collaboration Applications; Netiquette and Legal Issues; Spreadsheet and Database Applications; Security; Formatting, Grammar, Punctuation, Spelling, and Proofreading\n" +
                    "\n" +
                    "Skills: This event provides recognition for FBLA members who can most efficiently demonstrate computer application skills.", CompType.PRODUCTION, R.drawable.production_icon),
            new Competition("Computer Game and Simulation Programming", "Includes a demonstration of the project. Review specific guidelines for each event as guidelines vary.\n" +
                    "\n" +
                    "Topic: Develop a 2D side scrolling game about the FBLA Business Achievement Awards (BAA) Program.\n" +
                    "\n" +
                    "Give the game a name. The game must have a winning condition (points). You must implement a system of rewards (tokens), obstacles (penalties), a minimum of four levels, and lives. There must be an increase in difficulty as the levels increase.\n" +
                    "The game should be secure and bug free.\n" +
                    "The game should utilize two of the following: keyboard, touchscreen, and/or mouse.\n" +
                    "The game must be compatible for a maximum ESRB rating of E10+.\n" +
                    "The game should have an instructional display.\n" +
                    "The game should have a menu with start and exit/quit at any point and a score board at the end.\n" +
                    "Skills: This event tests the programmer’s skill in designing a functional interactive simulation/game that will both entertain and educate/inform the player.", CompType.TECH, R.drawable.tech_icon),
            new Competition("Computer Problem Solving", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: Operating Systems; Networks; Personal Computer Components; Security; Safety and Environmental Issues; Laptop and Portable Devices; Printers and Scanners\n" +
                    "\n" +
                    "Skills: This event provides recognition for FBLA members who have a broad base of knowledge and competency in core hardware and operating system technologies including installation, configuration, diagnostics, preventative maintenance, and basic networking.", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Cyber Security", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: Defend and Attack (virus, spam, spyware); Network Security; Disaster Recovery; Email Security; Intrusion Detection; Authentication; Public Key; Physical Security; Cryptography; Forensics Security; Cyber Security Policy\n" +
                    "\n" +
                    "Skills: This event provides recognition for FBLA members who understand security needs for technology.", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Database Design and Applications", "Two (2) parts: a production test administered and proctored at a designated school-site prior to the NLC and a 60-minute objective test administered onsite at NLC. Competitors must complete both parts for award eligibility.\n" +
                    "\n" +
                    "Production Test Competencies: Multiple Table Database Design; Table Creation, Inserting Data into Tables; Table SQL Statements; Creation of Forms/Reports\n" +
                    "\n" +
                    "Objective Test Competencies: Data Definitions/Terminologies; Query Development; Table Relationships; Form Development; Reports and Forms\n" +
                    "\n" +
                    "Skills: This event recognizes FBLA members who demonstrate that they have acquired entry level skills for understanding database usage and development in business.", CompType.PRODUCTION, R.drawable.production_icon),
            new Competition("Digital Video Production", "Two (2) parts: a prejudged project and a presentation. Competitors must complete both parts for award eligibility.\n" +
                    "\n" +
                    "Topic: Create a video promoting a new discount airline. The airline serves the states surrounding the one in which you live. The video should promote the new airline, include a theme/slogan, share information about flight schedules, and describe the frequent flyer program.\n" +
                    "\n" +
                    "Skills: This event provides recognition to FBLA members who demonstrate the ability to create an effective video to present an idea to a specific audience.", CompType.TECH, R.drawable.tech_icon),
            new Competition("E-Business", "", CompType.TECH, R.drawable.tech_icon),
            new Competition("Economics", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Electronic Career Portfolio", "", CompType.TECH, R.drawable.tech_icon),
            new Competition("Emerging Business Issues", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Entrepreneurship", "", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Future Business Leader", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Global Business", "", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Graphic Design", "", CompType.TECH, R.drawable.tech_icon),
            new Competition("Health Care Administration", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Help Desk", "", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Hospitality Management", "", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Impromptu Speaking", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Insurance and Risk Management", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to Business", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to Business Communication", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to Business Presentation", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Introduction to Business Procedures", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to FBLA", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to Financial Math", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to Information Technology", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to Parliamentary Procedure", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to Public Speaking", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Job Interview", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Journalism", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("LifeSmarts", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Local Chapter Annual Business Report", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Management Decision Making", "", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Management Information Systems", "", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Marketing", "", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Mobile Application Development", "", CompType.TECH, R.drawable.tech_icon),
            new Competition("Network Design", "", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Networking Concepts", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Organizational Leadership", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Parliamentary Procedure", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Partnership with Business Project", "", CompType.PROJECT, R.drawable.project_icon),
            new Competition("Personal Finance", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Political Science", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Public Service Announcement", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Public Speaking", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Publication Design", "", CompType.TECH, R.drawable.tech_icon),
            new Competition("Sales Presentation", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Securities and Investments", "", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Social Media Campaign", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Sports and Entertainment Management", "", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Spreadsheet Applications", "", CompType.PRODUCTION, R.drawable.production_icon),
            new Competition("Virtual Business Finance Challenge", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Virtual Business Management Challenge", "", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Website Design", "", CompType.TECH, R.drawable.tech_icon),
            new Competition("Word Processing", "", CompType.PRODUCTION, R.drawable.production_icon),

    };


    public static CompsFragment newInstance() {
        return new CompsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_comps, container, false);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        searchView = root.findViewById(R.id.compsSearch);
        eventRecyclerView = root.findViewById(R.id.comps);
        eventRecyclerView.setHasFixedSize(true);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        comps = new ArrayList<Competition>();
        for(Competition comp : competitions) {
            comps.add(comp);
        }
        adapter = new CompsAdapter(comps, root.getContext());
        eventRecyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CompsViewModel.class);
        // TODO: Use the ViewModel
    }

}

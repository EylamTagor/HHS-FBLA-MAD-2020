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
            new Competition("E-Business", "Includes a demonstration of the project. Review specific guidelines for each event as guidelines vary.\n" +
                    "\n" +
                    "Topic: Create an E-business website for a local rental business that will also do business online.  The business should be specifically targeted for your community.  The E-business site should include a name for the business and what items you will be renting.  Include pictures and descriptions of items for rent.  The site needs to include purchase and shipping information, a shopping cart, and social media links.  (NOTE:  No live social media accounts should be created for this event.)\n" +
                    "\n" +
                    "Skills: This event recognizes FBLA members who have developed proficiency in the creation and design of web commerce sites.", CompType.TECH, R.drawable.tech_icon),
            new Competition("Economics", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: Basic Economic Concepts and Principles; Monetary and Fiscal Policy; Productivity; Macroeconomics; Market Structures; Investments and Interest Rates; Government Role; Types of Businesses/Economic Institutions; Business Cycles/Circular Flow; Supply & Demand; International Trade/Global Economics\n" +
                    "\n" +
                    "Skills: This event provides recognition for FBLA members who can identify, understand, and apply economic principles to contemporary social, political, and ecological problems.\n" +
                    "\n", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Electronic Career Portfolio", "Includes a presentation. Review specific guidelines for each event as guidelines vary.\n" +
                    "\n" +
                    "Skills: An electronic career portfolio is a purposeful collection of work that tells the story of an applicant including achievements, growth, vision, reflection, skills, experience, education, training, and career goals. It is a tool that gives employers a complete picture of who you are—your experience, your education, your accomplishments—and what you have the potential to become; it is much more than what a mere letter of application and résumé can provide.", CompType.TECH, R.drawable.tech_icon),
            new Competition("Emerging Business Issues", "Includes a presentation or role play. Review specific guidelines for each event as guidelines vary.\n" +
                    "\n" +
                    "Topic: The traditional work environment is changing as technology provides employees the ability to work from anywhere.  How is this trend positively and negatively, affecting the modern business environment and employee collaboration?\n" +
                    "\n" +
                    "Skills: This event provides FBLA members with an opportunity to develop and demonstrate skills in researching and presenting an emerging business issue.", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Entrepreneurship", "Two (2) parts: an objective test and interactive role play or presentation. A 60-minute objective test will be administered onsite at the NLC. Team competitors will take one (1) objective test collaboratively.\n" +
                    "\n" +
                    "Objective Test Competencies: Business Plan; Community/Business Relations; Legal Issues; Initial Capital and Credit; Personnel Management; Financial Management; Marketing Management; Taxes; Government Regulations\n" +
                    "\n" +
                    "Case: A decision-making problem encountered by entrepreneurs in one (1) or more of the following areas: business planning, human relations, financial management, or marketing.\n" +
                    "\n" +
                    "Skills: Owning and managing a business is the goal of many Americans. This event recognizes FBLA members who demonstrate the knowledge and skills needed to establish and manage a business.\n" +
                    "\n", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Future Business Leader", "Multiple components: material submission prior to the conference, a preliminary interview, and a final interview. The Future Business Leader event also includes an objective test.\n" +
                    "\n" +
                    "Objective Test Competencies: FBLA Organization, Bylaws, and Handbook; National Competitive Event Guidelines; National Publications; Creed and National Goals; Business Knowledge, i.e., Accounting, Banking, Law, etc\n" +
                    "\n" +
                    "Skills: This event honors outstanding FBLA members who have demonstrated leadership qualities, participation in FBLA, and evidence of knowledge and skills essential for successful careers in business.\n" +
                    "\n", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Global Business", "Two (2) parts: an objective test and interactive role play or presentation. A 60-minute objective test will be administered onsite at the NLC. Team competitors will take one (1) objective test collaboratively.\n" +
                    "\n" +
                    "Objective Test Competencies: Basic International Concepts; Ownership and Management; Marketing; Finance; Communication (including culture and language); Treaties and Trade Agreements; Legal Issues; Human Resource Management; Ethics; Taxes and Government Regulations; Currency Exchange; International Travel; Career Development\n" +
                    "\n" +
                    "Case: A problem encountered in the international/global arena.\n" +
                    "\n" +
                    "Skills: The global economy is a complex, continually flowing, and constantly changing network of information, goods, services, and cultures. Most nations rely on other nations for natural resources to supply their needs and wants. Global business also provides new markets and investment opportunities as well as promotion of better relationships.", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Graphic Design", "Includes a presentation. Review specific guidelines for each event as guidelines vary.\n" +
                    "\n" +
                    "Topic: Your company has been hired to create the name for a new and upcoming music artist/band.  You and/or your team will develop the promotional/branding graphics for the new artist/band. The artist/band will need an identity with a name, logo, and webpage banner.  The package should also include graphics for a t-shirt, the cargo vehicle graphics (vehicle to move equipment from one performance to another), and the stage design.\n" +
                    "\n" +
                    "Skills: An essential part of today’s business world is commercial design and promotion; therefore, the preparation of computer-based digital art is paramount to the production of quality copy used for promotional purposes.\n" +
                    "\n", CompType.TECH, R.drawable.tech_icon),
            new Competition("Health Care Administration", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: Managing Office Procedures; Medical Terminology; Legal & Ethical Issues; Communication Skills; Managing Financial Functions; Health Insurance; Records Management; Infection Control; Medical History; Technology\n" +
                    "\n" +
                    "Skills: Health care administrators manage the business side of health services, ensuring effective use of resources to ensure the best medical care to the community. These skills include managing office activities, enhancing communication skills, identifying legal and ethical issues in healthcare practices, managing financial functions, and enhancing employability skills.\n" +
                    "\n", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Help Desk", "Two (2) parts: an objective test and interactive role play or presentation. A 60-minute objective test will be administered onsite at the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: Communication; Help Desk Operations and Procedures; Customer Management; Support Center Infrastructure and Procedures; Professional Career and Leadership Skills\n" +
                    "\n" +
                    "Case: An interactive role-play scenario will be given based on customer service in the technical field.\n" +
                    "\n" +
                    "Skills: This event provides recognition for FBLA members who demonstrate an understanding of and ability to provide technical assistance to end users. The ability to provide technical assistance to the users of computer hardware and software is essential to the success of any organization and its continued operation.\n" +
                    "\n", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Hospitality Management", "Two (2) parts: an objective test and interactive role play or presentation. A 60-minute objective test will be administered onsite at the NLC. Team competitors will take one (1) objective test collaboratively.\n" +
                    "\n" +
                    "Objective Test Competencies: Hospitality Marketing Concepts; Types of Hospitality Markets and Customers; Hospitality Operation and Management Functions; Customer Service in the Hospitality Industry; Human Resource Management in the Hospitality Industry; Legal Issues, Financial Management, and Budgeting for the Hospitality Industry; Current Hospitality Industry Trends; Environmental, Ethical, and Global Issues for the Hospitality Industry; Hotel Sales Process\n" +
                    "\n" +
                    "Case: A scenario in the hospitality management industry.\n" +
                    "\n" +
                    "Skills: Hospitality is an important aspect of business and society. This event provides recognition to FBLA members who have the ability to help other people enjoy both leisure and business-related events.", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Impromptu Speaking", "Business speech based on FBLA‑PBL Goals.\n" +
                    "\n" +
                    "Skills: The ability to express one’s thoughts without prior preparation is a valuable asset; as are poise, self-confidence, and organization of facts. This event recognizes FBLA members who develop qualities of business leadership by combining quick and clear thinking with conversational speaking.\n" +
                    "\n", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Insurance and Risk Management", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: Risk Management; Property and Liability Insurance; Health, Disability, and Life Insurance; Insurance Knowledge; Decision Making; Ethics; Careers\n" +
                    "\n" +
                    "Skills: This event provides recognition for FBLA members who demonstrate an understanding of and skill in basic insurance and risk management principles and procedures.", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to Business", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: Consumerism; Characteristics and Organization of Business; Money Management, Banking, and Investments; Rights and Responsibilities of Employees, Managers, Owners, and Government; Career Awareness; Insurance; Economic Systems; Ethics; Global (International) Business\n" +
                    "\n" +
                    "Skills: This event provides recognition for FBLA members who demonstrate an understanding of the American business enterprise system and its effect on consumers, employees, and entrepreneurs.", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to Business Communication", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: Grammar; Punctuation and Capitalization; Spelling; Proofreading & Editing; Word Definition and Usage; Oral Communication Concepts; Reading Comprehension\n" +
                    "\n" +
                    "Skills: Learning to communicate in a manner that is clearly understood by the receiver of the message is a major task of all businesspeople. This event provides recognition for FBLA members who demonstrate an understanding of basic communication skills and concepts.", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to Business Presentation", "Includes a presentation. Review specific guidelines for each event as guidelines vary.\n" +
                    "\n" +
                    "Topic: Prepare a presentation discussing how the news/media industry can recover from a tarnished image.\n" +
                    "\n" +
                    "Skills: This event provides recognition for FBLA members who demonstrate the ability to deliver an effective business presentation while using multimedia presentation technology.\n" +
                    "\n", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Introduction to Business Procedures", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: Human Relations; Technology Concepts; Communication Skills; Decision Making/Management; Career Development; Business Operations; Database/Information Management; Ethics/Safety; Finance; Information Processing\n" +
                    "\n" +
                    "Skills: This event provides recognition for FBLA members who possess knowledge of basic skills and procedures and the ability to make intelligent business decisions.", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to FBLA", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: FBLA Organization; Bylaws and Chapter Management Handbook; National Competitive Event Guidelines; National Publications; Creed and National Goals\n" +
                    "\n" +
                    "Skills: This event provides recognition for FBLA members who are interested in learning about the background of and current information of FBLA-PBL.", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to Financial Math", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: Basic Math Concepts; Consumer Credit; Data Analysis and Probability; Decimals; Discounts; Fractions; Percentages\n" +
                    "\n" +
                    "Skills: The ability to solve common financial and business mathematical problems is a basic skill required by all prospective business employees. This event provides recognition for FBLA members who have an understanding of basic math functions needed in finance and business.", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to Information Technology", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: Computer Hardware; Computer Software; Operating Systems; Common Program Functions; Word Processing; Spreadsheets; Presentation Software; Networking Concepts; Email and Electronic Communication; Internet Use\n" +
                    "\n" +
                    "Skills: Successful business leaders must understand the impact of technology and understand how to effectively harness it to drive their business success. This event recognizes FBLA members who demonstrate that they have acquired technology skills aligned with the Internet and Computing Core Certification (IC3) objectives.", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to Parliamentary Procedure", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: Parliamentary Procedure Principles; FBLA Bylaws\n" +
                    "\n" +
                    "Skills: This event recognizes FBLA members who demonstrate knowledge of basic principles of parliamentary procedure along with an understanding of FBLA’s organization and procedures.\n" +
                    "\n", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Introduction to Public Speaking", "Business speech based on FBLA‑PBL Goals.\n" +
                    "\n" +
                    "Skills: This event recognizes FBLA members who are beginning to develop qualities of business leadership by developing effective speaking skills.", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Job Interview", "Multiple components: material submission prior to the conference, a preliminary interview, and a final interview.\n" +
                    "\n" +
                    "Skills: This event recognizes FBLA members who demonstrate proficiency in applying for employment in business.", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Journalism", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: Basic Journalism Principles; Economics and Business of Journalism; Grammar & Format; Law and Ethics; History of Journalism\n" +
                    "\n" +
                    "Skills: This event recognizes FBLA members who demonstrate knowledge of the basic principles of journalism.", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("LifeSmarts", "This online event challenges students to integrate multiple areas of business knowledge and skills, using critical-thinking skills and teamwork during the competition. Student teams will compete online during the fall and spring competitions.\n" +
                    "\n" +
                    "Skills: The FBLA LifeSmarts encourages FBLA members to test their skills in economics, personal finance, and consumer issues. There are two challenges during the year (spring and fall). It is sponsored by the LifeSmarts program of the National Consumers League.\n" +
                    "\n", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Local Chapter Annual Business Report", "Two (2) parts: a prejudged report and a presentation. Competitors must complete both parts for award eligibility.\n" +
                    "\n" +
                    "Skills: The Hamden L. Forkner Award recognizes FBLA chapters that effectively summarize their year’s activities. The event provides participants with valuable experience in preparing annual business reports.", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Management Decision Making", "Two (2) parts: an objective test and interactive role play or presentation. A 60-minute objective test will be administered onsite at the NLC. Team competitors will take one (1) objective test collaboratively.\n" +
                    "\n" +
                    "Objective Test Competencies: Management Functions and Environment; Business Ownership and Law; Information and Communication Systems; Strategic Management; Human Resource Management; Ethics and Social Responsibility; Financial Management; Careers; Marketing; Economic Concepts; Business Operations\n" +
                    "\n" +
                    "Case: A problem encountered by managers in the following areas: human resource management, financial management, marketing management or information systems management. Competitors will assume the role of management and present a solution to the case study.\n" +
                    "\n" +
                    "Skills: Making critical decisions that provide the right direction and a winning position in today’s business world is essential to good management. Business executives must make high-quality, nearly instantaneous decisions all the time. The ability to make the right decisions concerning vision, growth, resources, strengths, and weaknesses leads to a successful business. It is management’s responsibility to manage for today and tomorrow, to manage for optimum efficiency, and to manage to compete in the marketplace.", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Management Information Systems", "Two (2) parts: an objective test and interactive role play or presentation. A 60-minute objective test will be administered onsite at the NLC. Team competitors will take one (1) objective test collaboratively.\n" +
                    "\n" +
                    "Objective Test Competencies: Systems Analysis & Design (Systems Development Life Cycle); Database Management and Modeling Concepts; Object Oriented Analysis and Design; User Interfaces; System Controls; Defining System and Business Requirements\n" +
                    "\n" +
                    "Case: A decision-making problem outlining a small business’ environment and needs. Competitors will analyze the situation and recommend an information system solution to address the issues raised.\n" +
                    "\n" +
                    "Skills: The ability to design and implement an information system solution to effectively manage vast amounts of information is a valuable skill that leads to the success of many business entities today. The use of technology to develop these information systems plays a crucial role in a business’ ability to compete in today’s business environment. This event provides recognition for FBLA members who demonstrate an understanding of and ability to apply these skills.", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Marketing", "Two (2) parts: an objective test and interactive role play or presentation. A 60-minute objective test will be administered onsite at the NLC. Team competitors will take one (1) objective test collaboratively.\n" +
                    "\n" +
                    "Objective Test Competencies: Basic Marketing Functions; Channels of Distribution; Legal, Ethical, and Social Aspects of Marketing; Promotion and Advertising Media; Marketing Information, Research, and Planning; E-Commerce; Economics; Selling and Merchandising\n" +
                    "\n" +
                    "Case: A marketing problem is proposed, and a solution is discussed.\n" +
                    "\n" +
                    "Skills: This event provides recognition for FBLA members who possess knowledge of the basic principles of marketing.", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Mobile Application Development", "Includes a demonstration of the project. Review specific guidelines for each event as guidelines vary.\n" +
                    "\n" +
                    "Topic: Develop an app for local chapters to manage their chapters.\n" +
                    "\n" +
                    "The app must include: App Name, About FBLA, Join FBLA (form), Calendar, links to FBLA websites, Local Officer Team, links to Social Media, Competitive Events, Current Events, Sign-up for a current event (form for either competitive event, fundraiser, or community service), Q & A, and Contact Us.\n" +
                    "The app must include a way to track chapter meeting attendance.\n" +
                    "The app must be designed for a phone/tablet.\n" +
                    "The operating system must be mobile based such as Android or iO.\n" +
                    "The app should state its licensing and terms of use.\n" +
                    "Skills: Mobile Applications are necessary to provide users with the ability to be productive while away from their computers. This event recognizes FBLA members who show an understanding in developing mobile apps.", CompType.TECH, R.drawable.tech_icon),
            new Competition("Network Design", "Two (2) parts: an objective test and interactive role play or presentation. A 60-minute objective test will be administered onsite at the NLC. Team competitors will take one (1) objective test collaboratively.\n" +
                    "\n" +
                    "Objective Test Competencies: Network Installation—Planning and Configuration; Problem Solving/Troubleshooting; Network Administrator Functions; Configuration of Internet Resources—Web service, DMZ, FTP, etc.; Backup and Disaster Recovery; Configuration Network Resources & Services\n" +
                    "\n" +
                    "Case: An analysis of a computing environment situation and recommendation for a network solution that addresses the issues provided.\n" +
                    "\n" +
                    "Skills: The ability to evaluate the needs of an organization and then design and implement network solutions is a valuable skill in today’s connected workplace. This event provides recognition for FBLA members who demonstrate an understanding of and ability to apply these skills.", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Networking Concepts", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: General Network Terminology and Concepts; Network Operating System Concepts; Network Security; Equipment for Network Access (Wi-Fi, wireless); OSI Model Functionality; Network Topologies & Connectivity\n" +
                    "\n" +
                    "Skills: Acquiring a high level of familiarization and proficiency in working with networks is essential in today’s connected workplace. This event provides recognition for FBLA members who have an understanding of network technologies.", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Organizational Leadership", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: Leadership Concepts; Leadership Managerial Roles; Behavior and Motivation; Networking; Communication Skills; Leader and Follower Relations; Team Leadership; Self-Managed Teams; Strategic Leadership for Managing Crises and Change; Levels of Leadership; Leadership Theory; Traits of Effective Leaders; Personality Profile of Effective Leaders; Leadership Attitudes; Ethical Leadership; Relationship Between Power, Politics, Networking, and Negotiation; Coaching; Managing Conflict; Team Decision Making; Organizational Politics; Team Skills; Charismatic and Transformational Leadership; Stewardship and Servant Leadership; Diverse Setting\n" +
                    "\n" +
                    "Skills: A dual focused management approach that works towards what is best for individuals and what is best for a group as a whole is the focus of organizational leadership. It is also an attitude and a work ethic that empowers an individual in any role to lead from the top, middle, or bottom of an organization. This event provides recognition for FBLA members who have an understanding of leadership within business organizations.", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Parliamentary Procedure", "Two (2) parts: an objective test and a meeting presentation. A 60-minute objective test will be administered onsite at the NLC. Team competitors will take individual tests and the individual scores will be averaged to determine the team score.\n" +
                    "\n" +
                    "Objective Test Competencies: Parliamentary Procedure Principles (questions will be drawn from the National Association of Parliamentarian’s official test bank); FBLA Bylaws\n" +
                    "\n" +
                    "Case: The role play scenario will be given to simulate a regular chapter meeting. The examination and performance criteria for this event will be based on Robert's Rules of Order Newly Revised, 11th edition.\n" +
                    "\n" +
                    "National Parliamentarian Candidate: The highest scoring underclassman on the parliamentary procedures exam who submits an officer application and meets all appropriate criteria becomes the new national parliamentarian.\n" +
                    "\n" +
                    "Skills: The Dorothy L. Travis Award recognizes FBLA members who demonstrate knowledge of parliamentary procedure principles along with an understanding of FBLA’s organization and procedures. This event is based on team rather than individual competition. Team participants develop speaking ability and poise through competitive performance.", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Partnership with Business Project", "Two (2) parts: a prejudged report and a presentation. Competitors must complete both parts for award eligibility.\n" +
                    "\n" +
                    "Skills: The purpose of this project is to learn about a business through communication and interaction with the business community.", CompType.PROJECT, R.drawable.project_icon),
            new Competition("Personal Finance", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: Financial Principles Related to Personal Decision Making; Managing Budgets and Finance (Planning and Money Management); Earning a Living (Income, Taxes); Buying Goods and Services; Saving and Investing; Banking and Insurance; Credit and Debt\n" +
                    "\n" +
                    "Skills: This event recognizes students, who possess essential knowledge and skills related to financial issues, can analyze the rights and responsibilities of consumers, and apply knowledge to financial situations.", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Political Science", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: Political Science Terms & Concepts; History & Role of Political Science; Civil Liberties & Civil Rights in Political Science; Forms of Government & Legislatures; Electoral Systems & Presidential Elections; The Powers & Elections of Congress; Federal Judicial System; Federal Bureaucracy; Mass Media & Politics; Public Opinion & Culture; Political Science Law; Public & Social Policy; Government Fiscal Policy; Government Foreign & Defense Policies; International Relations Concepts\n" +
                    "\n" +
                    "Skills: This event provides recognition for FBLA members who show an understanding of the government’s role in society and the interaction between economic and political life.", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Public Service Announcement", "Includes a presentation. Review specific guidelines for each event as guidelines vary.\n" +
                    "\n" +
                    "Topic: Create a Public Service Announcement about the importance of financial literacy for teens.\n" +
                    "\n" +
                    "Skills: This recognizes FBLA members who can research a topic and create a 30-second PSA video.", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Public Speaking", "Business speech based on FBLA‑PBL Goals.\n" +
                    "\n" +
                    "Skills: This event recognizes FBLA members who are developing qualities of business leadership by cultivating effective speaking skills.", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Publication Design", "Includes a presentation. Review specific guidelines for each event as guidelines vary.\n" +
                    "\n" +
                    "Topic: Your company has been hired to create the name for a new and upcoming music artist/band.  You and/or your team will develop publication items for the new artist/band. You and/or your team will create the name and logo of the artist/band.  In addition, an event poster, an event banner, an event venue setup/layout, and a news release must be created.\n" +
                    "\n" +
                    "Skills: This event provides recognition to FBLA members who can most effectively demonstrate skills in the area of print publication using creativity and decision-making skills.", CompType.TECH, R.drawable.tech_icon),
            new Competition("Sales Presentation", "Includes a presentation. Review specific guidelines for each event as guidelines vary.\n" +
                    "\n" +
                    "Skills: This event provides recognition to FBLA members who can effectively deliver a pitch that attempts to persuade someone with a planned sales presentation strategy of a product or service designed to initiate and close a sale.", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Securities and Investments", "60-minute test administered during the NLC.\n" +
                    "\n" +
                    "Objective Test Competencies: Investment Fundamentals; Investment Wrappers, Taxation, and Trusts; Investment Product & Funds; Stock Market; Stock versus Other Investments; Mutual Funds; Basics of Bonds; Derivatives; Financial Services Regulation\n" +
                    "\n" +
                    "Skills: Acquiring a high level of familiarization and knowledge of securities and investing is valuable in planning for one’s future. This event provides recognition for FBLA members who have an understanding of securities and investments.", CompType.WRITTEN, R.drawable.written_icon),
            new Competition("Social Media Campaign", "Includes a presentation. Review specific guidelines for each event as guidelines vary.\n" +
                    "\n" +
                    "Topic: Financial literacy is important for financial health in the future. Plan a social media campaign to increase an FBLA member’s knowledge of finances and the impact of personal financial literacy training.  (NOTE:  No live social media accounts should be created for this event.)\n" +
                    "\n" +
                    "Skills: Social media marketing is a form of Internet marketing that utilizes social networking websites as a marketing tool. The goal is to produce content that users will share with their social networks to help a company increase brand exposure and broaden customer reach. This event provides recognition to FBLA members who can most effectively demonstrate skill in the area of social media marketing.", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Sports and Entertainment Management", "Two (2) parts: an objective test and interactive role play or presentation. A 60-minute objective test will be administered onsite at the NLC. Team competitors will take one (1) objective test collaboratively.\n" +
                    "\n" +
                    "Objective Test Competencies: Sports and Entertainment Marketing/Strategic Marketing; Facility and Event Management; Human Resource Management (Labor Relations); Promotion, Advertising, and Sponsorship for Sports and Entertainment Industry; Financing and Economic Input; Planning, Distribution, Marketing, Pricing, and Selling Sports and Entertainment Events; Sports Law; Communication in Sports and Entertainment (Media); Ethics; Licensing; Sports Governance; Careers; Marketing/Management Information Technology and Research; Leadership and Managing Groups and Teams in the Sports and Entertainment Industry; Management Strategies and Strategic Planning Tools; Basic Functions of Management\n" +
                    "\n" +
                    "Case: A problem outlining the understanding and awareness of sports and entertainment issues within today’s society.\n" +
                    "\n" +
                    "Skills: This event provides recognition for FBLA members who possess skill in the basic principles of sports and entertainment management.\n" +
                    "\n", CompType.CASESTUDY, R.drawable.casestudy_icon),
            new Competition("Spreadsheet Applications", "Two (2) parts: a production test administered and proctored at a designated school-site prior to the NLC and a 60-minute objective test administered onsite at NLC. Competitors must complete both parts for award eligibility.\n" +
                    "\n" +
                    "Production Test Competencies: Basic Mathematical Concepts; Data Organization Concepts; Creating Formulas; Functions; Generate Graphs for Analysis Purposes; Pivot Tables; Create Macros; Filter and Extract Data\n" +
                    "\n" +
                    "Objective Test Competencies: Formulas; Functions; Graphics, Charts, and Reports; Pivot Tables and Advanced Tools; Macros and Templates; Filters and Extraction of Data; Format and Print Options; Purpose for Spreadsheets\n" +
                    "\n" +
                    "Skills: Spreadsheet skills are necessary to convert data to information in business. This event recognizes FBLA members who demonstrate that they have acquired skills for spreadsheet development in business.\n" +
                    "\n", CompType.PRODUCTION, R.drawable.production_icon),
            new Competition("Virtual Business Finance Challenge", "This event is based on the Virtual Business Personal Finance web-based simulation. The VBC consists of two (2) challenges during the year (fall and spring). Members will use this simulation to test their skills at managing their own financial lives and will compete against students across the country. Students file taxes, open bank accounts, apply for credit cards, monitor credit scores, apply for jobs, purchase insurance, and more. The online simulation is 100 percent web based.\n" +
                    "\n" +
                    "Skills: The FBLA Virtual Business Finance Challenge encourages FBLA members to test their financial literacy skills. Participating teams will be making personal finance decisions for a simulated person. The concepts students will be managing include opening bank accounts, paying bills, filing taxes, applying for jobs, enrolling in educational courses, paying for goods, applying for credit cards, determining schedules, budgeting, and more.", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Virtual Business Management Challenge", "This event is based on the Virtual Business Management web-based simulation where FBLA students test their skills at managing businesses individually or as a team. No downloads are required for this online application. The VBC consists of two (2) challenges during the year (fall and spring), and each challenge focuses on different business concepts such as recruiting job candidates, hiring & supervising employees, productivity & efficiency, risk management and more.\n" +
                    "\n" +
                    "Skills: The FBLA Virtual Business Management Challenge encourages FBLA members to test their skills at managing a business. Students will be limited as to which concepts they are able to control during each of the qualifying rounds. What participants control will include various combinations of the following concepts: recruiting/hiring/supervising employees, risk management, organizing floor layouts, bidding on orders and more.\n" +
                    "\n", CompType.SPEAKING, R.drawable.speaking_icon),
            new Competition("Website Design", "Includes a demonstration of the project. Review specific guidelines for each event as guidelines vary.\n" +
                    "\n" +
                    "Topic: Create a website for a new discount airline. This airline serves the states surrounding the one in which you live. The airline has hired you to create a website. The website must include:\n" +
                    "\n" +
                    "Airline name and theme/slogan\n" +
                    "Introduction to the airline that includes animation\n" +
                    "Flight schedules, with the ability to book flights\n" +
                    "Information on the frequent flyer program\n" +
                    "How to apply for a job with the airline\n" +
                    "Social media links (NOTE: No live social media accounts should be created for this event.)\n" +
                    "Skills: The ability to communicate ideas and concepts and to deliver value to customers, using the Internet and related technologies is an important element in a business’ success. This event recognizes FBLA members who have developed proficiency in the creation and design of websites.", CompType.TECH, R.drawable.tech_icon),
            new Competition("Word Processing", "Two (2) parts: a production test administered and proctored at a designated school-site prior to the NLC and a 60-minute objective test administered onsite at NLC. Competitors must complete both parts for award eligibility.\n" +
                    "\n" +
                    "Production Test Competencies: Production of All Types of Business Forms; Letters and Mail Merge; Memos; Tables; Reports (including statistical); Materials from Rough Draft and Unarranged Copy; Email Messages\n" +
                    "\n" +
                    "Objective Test Competencies: Basic Keyboarding Terminology and Concepts; Related Application Knowledge; Advanced Applications; Document Formatting Rules and Standards; Grammar, Punctuation, Spelling, and Proofreading; Printing\n" +
                    "\n" +
                    "Skills: A high level of word processing skill is a necessity for employees in productive offices. This event recognizes FBLA members who demonstrate that they have acquired word processing proficiency beyond the entry level.\n" +
                    "\n", CompType.PRODUCTION, R.drawable.production_icon),

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

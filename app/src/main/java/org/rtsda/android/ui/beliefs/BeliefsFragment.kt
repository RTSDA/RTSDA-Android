package org.rtsda.android.ui.beliefs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.rtsda.android.R
import org.rtsda.android.databinding.FragmentBeliefsBinding
import org.rtsda.android.ui.beliefs.adapter.BeliefsAdapter
import org.rtsda.android.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

data class Verse(
    val reference: String,
    val text: String
)

data class Belief(
    val id: Int,
    val title: String,
    val summary: String,
    val verses: List<Verse>
)

@AndroidEntryPoint
class BeliefsFragment : Fragment() {

    private var _binding: FragmentBeliefsBinding? = null
    private val binding get() = _binding!!

    private val adapter = BeliefsAdapter { belief ->
        showVersesDialog(belief)
    }

    private fun showVersesDialog(belief: Belief) {
        val versesText = belief.verses.joinToString("\n\n") { verse ->
            "${verse.reference}\n${verse.text}"
        }

        MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
            .setTitle(belief.title)
            .setMessage(versesText)
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    companion object {
        val beliefs = listOf(
            Belief(
                id = 1,
                title = "The Holy Scriptures",
                summary = "The Holy Scriptures, Old and New Testaments, are the written Word of God, given by divine inspiration through holy men of God who spoke and wrote as they were moved by the Holy Spirit.",
                verses = listOf(
                    Verse("2 Peter 1:20-21", "Knowing this first, that no prophecy of the scripture is of any private interpretation. For the prophecy came not in old time by the will of man: but holy men of God spake as they were moved by the Holy Ghost."),
                    Verse("2 Timothy 3:16-17", "All scripture is given by inspiration of God, and is profitable for doctrine, for reproof, for correction, for instruction in righteousness: That the man of God may be perfect, thoroughly furnished unto all good works."),
                    Verse("Psalm 119:105", "Thy word is a lamp unto my feet, and a light unto my path.")
                )
            ),
            Belief(
                id = 2,
                title = "The Trinity",
                summary = "There is one God: Father, Son, and Holy Spirit, a unity of three co-eternal Persons.",
                verses = listOf(
                    Verse("Matthew 28:19", "Go ye therefore, and teach all nations, baptizing them in the name of the Father, and of the Son, and of the Holy Ghost."),
                    Verse("2 Corinthians 13:14", "The grace of the Lord Jesus Christ, and the love of God, and the communion of the Holy Ghost, be with you all. Amen.")
                )
            ),
            Belief(
                id = 3,
                title = "The Father",
                summary = "God the eternal Father is the Creator, Source, Sustainer, and Sovereign of all creation.",
                verses = listOf(
                    Verse("Genesis 1:1", "In the beginning God created the heaven and the earth."),
                    Verse("John 3:16", "For God so loved the world, that he gave his only begotten Son, that whosoever believeth in him should not perish, but have everlasting life.")
                )
            ),
            Belief(
                id = 4,
                title = "The Son",
                summary = "God the eternal Son became incarnate in Jesus Christ. Through Him all things were created, the character of God is revealed, the salvation of humanity is accomplished, and the world is judged.",
                verses = listOf(
                    Verse("John 1:1-3", "In the beginning was the Word, and the Word was with God, and the Word was God. The same was in the beginning with God. All things were made by him; and without him was not any thing made that was made."),
                    Verse("John 3:16", "For God so loved the world, that he gave his only begotten Son, that whosoever believeth in him should not perish, but have everlasting life.")
                )
            ),
            Belief(
                id = 5,
                title = "The Holy Spirit",
                summary = "God the eternal Spirit was active with the Father and the Son in Creation, incarnation, and redemption. He inspired the writers of Scripture. He filled Christ's life with power.",
                verses = listOf(
                    Verse("Genesis 1:2", "And the earth was without form, and void; and darkness was upon the face of the deep. And the Spirit of God moved upon the face of the waters."),
                    Verse("John 14:16-17", "And I will pray the Father, and he shall give you another Comforter, that he may abide with you for ever; Even the Spirit of truth; whom the world cannot receive, because it seeth him not, neither knoweth him: but ye know him; for he dwelleth with you, and shall be in you.")
                )
            ),
            Belief(
                id = 6,
                title = "Creation",
                summary = "God is Creator of all things, and has revealed in Scripture the authentic account of His creative activity.",
                verses = listOf(
                    Verse("Genesis 1:1", "In the beginning God created the heaven and the earth."),
                    Verse("Exodus 20:11", "For in six days the LORD made heaven and earth, the sea, and all that in them is, and rested the seventh day: wherefore the LORD blessed the sabbath day, and hallowed it.")
                )
            ),
            Belief(
                id = 7,
                title = "The Nature of Man",
                summary = "Man and woman were made in the image of God with individuality, the power and freedom to think and to do.",
                verses = listOf(
                    Verse("Genesis 1:26-27", "And God said, Let us make man in our image, after our likeness: and let them have dominion over the fish of the sea, and over the fowl of the air, and over the cattle, and over all the earth, and over every creeping thing that creepeth upon the earth. So God created man in his own image, in the image of God created he him; male and female created he them."),
                    Verse("Psalm 8:4-6", "What is man, that thou art mindful of him? and the son of man, that thou visitest him? For thou hast made him a little lower than the angels, and hast crowned him with glory and honour. Thou madest him to have dominion over the works of thy hands; thou hast put all things under his feet.")
                )
            ),
            Belief(
                id = 8,
                title = "The Great Controversy",
                summary = "All humanity is now involved in a great controversy between Christ and Satan regarding the character of God, His law, and His sovereignty over the universe.",
                verses = listOf(
                    Verse("Revelation 12:7-9", "And there was war in heaven: Michael and his angels fought against the dragon; and the dragon fought and his angels, And prevailed not; neither was their place found any more in heaven. And the great dragon was cast out, that old serpent, called the Devil, and Satan, which deceiveth the whole world: he was cast out into the earth, and his angels were cast out with him."),
                    Verse("Job 1:6-12", "Now there was a day when the sons of God came to present themselves before the LORD, and Satan came also among them. And the LORD said unto Satan, Whence comest thou? Then Satan answered the LORD, and said, From going to and fro in the earth, and from walking up and down in it.")
                )
            ),
            Belief(
                id = 9,
                title = "The Life, Death, and Resurrection of Christ",
                summary = "In Christ's life of perfect obedience to God's will, His suffering, death, and resurrection, God provided the only means of atonement for human sin.",
                verses = listOf(
                    Verse("John 3:16", "For God so loved the world, that he gave his only begotten Son, that whosoever believeth in him should not perish, but have everlasting life."),
                    Verse("1 Corinthians 15:3-4", "For I delivered unto you first of all that which I also received, how that Christ died for our sins according to the scriptures; And that he was buried, and that he rose again the third day according to the scriptures.")
                )
            ),
            Belief(
                id = 10,
                title = "The Experience of Salvation",
                summary = "In infinite love and mercy God made Christ, who knew no sin, to be sin for us, so that in Him we might be made the righteousness of God.",
                verses = listOf(
                    Verse("2 Corinthians 5:17-21", "Therefore if any man be in Christ, he is a new creature: old things are passed away; behold, all things are become new. And all things are of God, who hath reconciled us to himself by Jesus Christ, and hath given to us the ministry of reconciliation; To wit, that God was in Christ, reconciling the world unto himself, not imputing their trespasses unto them; and hath committed unto us the word of reconciliation."),
                    Verse("John 3:3-8", "Jesus answered and said unto him, Verily, verily, I say unto thee, Except a man be born again, he cannot see the kingdom of God.")
                )
            ),
            Belief(
                id = 11,
                title = "Growing in Christ",
                summary = "By His death on the cross Jesus triumphed over the forces of evil. He who subjugated the demonic spirits during His earthly ministry has broken their power.",
                verses = listOf(
                    Verse("John 15:4-5", "Abide in me, and I in you. As the branch cannot bear fruit of itself, except it abide in the vine; no more can ye, except ye abide in me. I am the vine, ye are the branches: He that abideth in me, and I in him, the same bringeth forth much fruit: for without me ye can do nothing."),
                    Verse("Philippians 4:13", "I can do all things through Christ which strengtheneth me.")
                )
            ),
            Belief(
                id = 12,
                title = "The Church",
                summary = "The church is the community of believers who confess Jesus Christ as Lord and Saviour.",
                verses = listOf(
                    Verse("Ephesians 4:11-15", "And he gave some, apostles; and some, prophets; and some, evangelists; and some, pastors and teachers; For the perfecting of the saints, for the work of the ministry, for the edifying of the body of Christ: Till we all come in the unity of the faith, and of the knowledge of the Son of God, unto a perfect man, unto the measure of the stature of the fulness of Christ."),
                    Verse("Matthew 16:18", "And I say also unto thee, That thou art Peter, and upon this rock I will build my church; and the gates of hell shall not prevail against it.")
                )
            ),
            Belief(
                id = 13,
                title = "The Remnant and Its Mission",
                summary = "The universal church is composed of all who truly believe in Christ, but in the last days, a time of widespread apostasy, a remnant has been called out to keep the commandments of God and the faith of Jesus.",
                verses = listOf(
                    Verse("Revelation 12:17", "And the dragon was wroth with the woman, and went to make war with the remnant of her seed, which keep the commandments of God, and have the testimony of Jesus Christ."),
                    Verse("Revelation 14:12", "Here is the patience of the saints: here are they that keep the commandments of God, and the faith of Jesus.")
                )
            ),
            Belief(
                id = 14,
                title = "Unity in the Body of Christ",
                summary = "The church is one body with many members, called from every nation, kindred, tongue, and people.",
                verses = listOf(
                    Verse("1 Corinthians 12:12-14", "For as the body is one, and hath many members, and all the members of that one body, being many, are one body: so also is Christ. For by one Spirit are we all baptized into one body, whether we be Jews or Gentiles, whether we be bond or free; and have been all made to drink into one Spirit. For the body is not one member, but many."),
                    Verse("John 17:20-23", "Neither pray I for these alone, but for them also which shall believe on me through their word; That they all may be one; as thou, Father, art in me, and I in thee, that they also may be one in us: that the world may believe that thou hast sent me.")
                )
            ),
            Belief(
                id = 15,
                title = "Baptism",
                summary = "By baptism we confess our faith in the death and resurrection of Jesus Christ, and testify of our death to sin and of our purpose to walk in newness of life.",
                verses = listOf(
                    Verse("Matthew 28:19-20", "Go ye therefore, and teach all nations, baptizing them in the name of the Father, and of the Son, and of the Holy Ghost: Teaching them to observe all things whatsoever I have commanded you: and, lo, I am with you alway, even unto the end of the world. Amen."),
                    Verse("Romans 6:1-6", "What shall we say then? Shall we continue in sin, that grace may abound? God forbid. How shall we, that are dead to sin, live any longer therein? Know ye not, that so many of us as were baptized into Jesus Christ were baptized into his death?")
                )
            ),
            Belief(
                id = 16,
                title = "The Lord's Supper",
                summary = "The Lord's Supper is a participation in the emblems of the body and blood of Jesus as an expression of faith in Him, our Lord and Saviour.",
                verses = listOf(
                    Verse("1 Corinthians 11:23-26", "For I have received of the Lord that which also I delivered unto you, That the Lord Jesus the same night in which he was betrayed took bread: And when he had given thanks, he brake it, and said, Take, eat: this is my body, which is broken for you: this do in remembrance of me. After the same manner also he took the cup, when he had supped, saying, This cup is the new testament in my blood: this do ye, as oft as ye drink it, in remembrance of me."),
                    Verse("Matthew 26:26-28", "And as they were eating, Jesus took bread, and blessed it, and brake it, and gave it to the disciples, and said, Take, eat; this is my body. And he took the cup, and gave thanks, and gave it to them, saying, Drink ye all of it; For this is my blood of the new testament, which is shed for many for the remission of sins.")
                )
            ),
            Belief(
                id = 17,
                title = "Spiritual Gifts and Ministries",
                summary = "God bestows upon all members of His church in every age spiritual gifts which each member is to employ in loving ministry for the common good of the church and of humanity.",
                verses = listOf(
                    Verse("1 Corinthians 12:4-11", "Now there are diversities of gifts, but the same Spirit. And there are differences of administrations, but the same Lord. And there are diversities of operations, but it is the same God which worketh all in all. But the manifestation of the Spirit is given to every man to profit withal."),
                    Verse("Ephesians 4:8", "Wherefore he saith, When he ascended up on high, he led captivity captive, and gave gifts unto men.")
                )
            ),
            Belief(
                id = 18,
                title = "The Gift of Prophecy",
                summary = "The Scriptures testify that one of the gifts of the Holy Spirit is prophecy. This gift is an identifying mark of the remnant church.",
                verses = listOf(
                    Verse("Revelation 12:17", "And the dragon was wroth with the woman, and went to make war with the remnant of her seed, which keep the commandments of God, and have the testimony of Jesus Christ."),
                    Verse("Revelation 19:10", "And I fell at his feet to worship him. And he said unto me, See thou do it not: I am thy fellowservant, and of thy brethren that have the testimony of Jesus: worship God: for the testimony of Jesus is the spirit of prophecy.")
                )
            ),
            Belief(
                id = 19,
                title = "The Law of God",
                summary = "The great principles of God's law are embodied in the Ten Commandments and exemplified in the life of Christ.",
                verses = listOf(
                    Verse("Exodus 20:1-17", "And God spake all these words, saying, I am the LORD thy God, which have brought thee out of the land of Egypt, out of the house of bondage. Thou shalt have no other gods before me..."),
                    Verse("Matthew 5:17-18", "Think not that I am come to destroy the law, or the prophets: I am not come to destroy, but to fulfil. For verily I say unto you, Till heaven and earth pass, one jot or one tittle shall in no wise pass from the law, till all be fulfilled.")
                )
            ),
            Belief(
                id = 20,
                title = "The Sabbath",
                summary = "The beneficent Creator, after the six days of Creation, rested on the seventh day and instituted the Sabbath for all people as a memorial of Creation.",
                verses = listOf(
                    Verse("Genesis 2:1-3", "And on the seventh day God ended his work which he had made; and he rested on the seventh day from all his work which he had made. And God blessed the seventh day, and sanctified it: because that in it he had rested from all his work which God created and made."),
                    Verse("Exodus 20:8-11", "Remember the sabbath day, to keep it holy. Six days shalt thou labour, and do all thy work: But the seventh day is the sabbath of the LORD thy God: in it thou shalt not do any work, thou, nor thy son, nor thy daughter, thy manservant, nor thy maidservant, nor thy cattle, nor thy stranger that is within thy gates: For in six days the LORD made heaven and earth, the sea, and all that in them is, and rested the seventh day: wherefore the LORD blessed the sabbath day, and hallowed it.")
                )
            ),
            Belief(
                id = 21,
                title = "Stewardship",
                summary = "We are God's stewards, entrusted by Him with time and opportunities, abilities and possessions, and the blessings of the earth and its resources.",
                verses = listOf(
                    Verse("1 Chronicles 29:14", "But who am I, and what is my people, that we should be able to offer so willingly after this sort? for all things come of thee, and of thine own have we given thee."),
                    Verse("Malachi 3:8-10", "Will a man rob God? Yet ye have robbed me. But ye say, Wherein have we robbed thee? In tithes and offerings. Ye are cursed with a curse: for ye have robbed me, even this whole nation.")
                )
            ),
            Belief(
                id = 22,
                title = "Christian Behavior",
                summary = "We are called to be a godly people who think, feel, and act in harmony with the principles of heaven.",
                verses = listOf(
                    Verse("1 John 2:6", "He that saith he abideth in him ought himself also so to walk, even as he walked."),
                    Verse("Philippians 4:8", "Finally, brethren, whatsoever things are true, whatsoever things are honest, whatsoever things are just, whatsoever things are pure, whatsoever things are lovely, whatsoever things are of good report; if there be any virtue, and if there be any praise, think on these things.")
                )
            ),
            Belief(
                id = 23,
                title = "Marriage and the Family",
                summary = "Marriage was divinely established in Eden and affirmed by Jesus to be a lifelong union between a man and a woman in loving companionship.",
                verses = listOf(
                    Verse("Genesis 2:18-24", "And the LORD God said, It is not good that the man should be alone; I will make him an help meet for him... Therefore shall a man leave his father and his mother, and shall cleave unto his wife: and they shall be one flesh."),
                    Verse("Matthew 19:4-6", "And he answered and said unto them, Have ye not read, that he which made them at the beginning made them male and female, And said, For this cause shall a man leave father and mother, and shall cleave to his wife: and they twain shall be one flesh? Wherefore they are no more twain, but one flesh. What therefore God hath joined together, let not man put asunder.")
                )
            ),
            Belief(
                id = 24,
                title = "Christ's Ministry in the Heavenly Sanctuary",
                summary = "There is a sanctuary in heaven, the true tabernacle which the Lord set up and not man. In it Christ ministers on our behalf.",
                verses = listOf(
                    Verse("Hebrews 8:1-2", "Now of the things which we have spoken this is the sum: We have such an high priest, who is set on the right hand of the throne of the Majesty in the heavens; A minister of the sanctuary, and of the true tabernacle, which the Lord pitched, and not man."),
                    Verse("Hebrews 9:11-12", "But Christ being come an high priest of good things to come, by a greater and more perfect tabernacle, not made with hands, that is to say, not of this building; Neither by the blood of goats and calves, but by his own blood he entered in once into the holy place, having obtained eternal redemption for us.")
                )
            ),
            Belief(
                id = 25,
                title = "The Second Coming of Christ",
                summary = "The second coming of Christ is the blessed hope of the church, the grand climax of the gospel.",
                verses = listOf(
                    Verse("Titus 2:13", "Looking for that blessed hope, and the glorious appearing of the great God and our Saviour Jesus Christ."),
                    Verse("John 14:1-3", "Let not your heart be troubled: ye believe in God, believe also in me. In my Father's house are many mansions: if it were not so, I would have told you. I go to prepare a place for you. And if I go and prepare a place for you, I will come again, and receive you unto myself; that where I am, there ye may be also.")
                )
            ),
            Belief(
                id = 26,
                title = "Death and Resurrection",
                summary = "The wages of sin is death. But God, who alone is immortal, will grant eternal life to His redeemed.",
                verses = listOf(
                    Verse("Romans 6:23", "For the wages of sin is death; but the gift of God is eternal life through Jesus Christ our Lord."),
                    Verse("1 Thessalonians 4:16-17", "For the Lord himself shall descend from heaven with a shout, with the voice of the archangel, and with the trump of God: and the dead in Christ shall rise first: Then we which are alive and remain shall be caught up together with them in the clouds, to meet the Lord in the air: and so shall we ever be with the Lord.")
                )
            ),
            Belief(
                id = 27,
                title = "The Millennium and the End of Sin",
                summary = "The millennium is the thousand-year reign of Christ with His saints in heaven between the first and second resurrections.",
                verses = listOf(
                    Verse("Revelation 20:1-6", "And I saw an angel come down from heaven, having the key of the bottomless pit and a great chain in his hand. And he laid hold on the dragon, that old serpent, which is the Devil, and Satan, and bound him a thousand years..."),
                    Verse("Revelation 21:1-5", "And I saw a new heaven and a new earth: for the first heaven and the first earth were passed away; and there was no more sea. And I John saw the holy city, new Jerusalem, coming down from God out of heaven, prepared as a bride adorned for her husband.")
                )
            ),
            Belief(
                id = 28,
                title = "The New Earth",
                summary = "On the new earth, in which righteousness dwells, God will provide an eternal home for the redeemed and a perfect environment for everlasting life, love, joy, and learning in His presence.",
                verses = listOf(
                    Verse("2 Peter 3:13", "Nevertheless we, according to his promise, look for new heavens and a new earth, wherein dwelleth righteousness."),
                    Verse("Revelation 21:1-4", "And I saw a new heaven and a new earth: for the first heaven and the first earth were passed away; and there was no more sea. And I John saw the holy city, new Jerusalem, coming down from God out of heaven, prepared as a bride adorned for her husband.")
                )
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBeliefsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeRefresh()
        loadBeliefs()
    }

    private fun setupRecyclerView() {
        binding.beliefsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.beliefsRecyclerView.adapter = adapter
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            loadBeliefs()
        }
    }

    private fun loadBeliefs() {
        adapter.submitList(beliefs)
        binding.swipeRefresh.isRefreshing = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
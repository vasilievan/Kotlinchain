package aleksey.vasiliev

import org.apache.commons.cli.UnrecognizedOptionException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import aleksey.vasiliev.ServerParameters.nodesToSend

class ApplicationTest {
    @Test
    fun checkFirstBlock() {
        RandomString.seed = 3141592653L
        nodesToSend = setOf("http://127.0.0.1:8081")
        val blockProducing = BlockProducing()
        var actualValue = blockProducing.produceFirstBlock()
        assertEquals(
            Block(
                "0",
                "0",
                "15399413692088809598794873290973252182218468600246205908895035468909751000000",
                "UPS8xtwbOHkr1PRydPVEiQdGPQK9V2DUe3UAEJ0nCinfuugeTy3H1tU1c4RApMuSJ922qpjtCw6ni6YOaGXuTyMb5v6ltVDXxipstD83fthpFzsn7jELO6oGi5PrNmccj6iovRST3goLT8PONa81oBWkcwWvSjFD8uRvPeZpPdIVMTEilGXkozzRtWC1JZdvkr2YyrVMcblCqOAq9v9YlSnDi0elocwAE89z4IZ9vov3T1UKKv4yvlRxeADfqgvX",
                "964593"
            ),
            actualValue
        )
        RandomString.seed = 0L
        actualValue = blockProducing.produceFirstBlock()
        assertEquals(
            Block(
                "0",
                "0",
                "2117463294697193899461360921095734410517801877994669064300316511988070000000",
                "TZoLNtIhLWupxM982b6krthwjRbaHa7IgpMrzaW89QE1U8TJYa0zGS39iHfl8tSPSdiwCraPeQvYvL158U1HLcltnm0fazQpV6SQncWy6VaMyL0bW59t2Dki7lS4PrXOBtCf0DFS9J8JvxcWeabgDa8D9vVSFNputU0TdlamvYf50ZaTtSGs4tr62VLH4jc8OtEmFEGmwxDIRnura7RVUTLDoJyf4ukKaKqd3UsnAxQxzQvkPvT03ZJaFctOHM8b",
                "109001"
            ),
            actualValue
        )
        RandomString.seed = -1782873342324L
        actualValue = blockProducing.produceFirstBlock()
        assertEquals(
            Block(
                "0",
                "0",
                "-35384413252161653515816080931839869828851003255795056908889561590543310000000",
                "U8ECF6H6m7FRwrTyGVjGaDmzx6irDf9G76dev6rvgdDVrtpppIqrsKPJdC749dJHZHhxXPbrWaqLCr7uVvWJKaLTXaPCyKgYtvodGQEmpnqG8sBlp4vFgaQGVPIzk1oubjvlpIPWJpshELfgUsKbNZvSp7qeVtpjFXNR2KyxIuoAxmRwPIixArHTgEWG8rVM8wXHiVsqdSuiDgQbZ4JCNKcl5RIZgHBqZlZUhoMnnIoHS4cINmdCgmYtGBlvBKIe",
                "2677062"
            ),
            actualValue
        )
    }

    @Test
    fun checkProduceBlock() {
        nodesToSend = setOf("http://127.0.0.1:8081")
        val blockProducing = BlockProducing()
        RandomString.seed = 3141592653L
        val firstBlock = blockProducing.produceFirstBlock()
        val secondBlock = blockProducing.produceBlock(firstBlock)
        assertEquals(
            Block(
                "1",
                "15399413692088809598794873290973252182218468600246205908895035468909751000000",
                "-16346848297816610177277472819678465847532546009992945195534181085709118000000",
                "w1MGc0NyVjEOft2YTYSJSW2QzWMt5NkKwif5KqJPW2XZqWQsgsMnJjQxexIxxNtu1jmzxUWg8MG5wpUnpTPz5Wenk8NGME4l0iRlY7hmbLJWSIChk9GOGDIn2Pn7BpbZZ98g4wPcjjaxAHeif0K6VDwflaHka0rikvtWVItXPmZMOsQC2zpqlZ650eeFMHELpKKwR0U2e3Oexf7MO1AR2Ij3oRc3brKKiuzvI8JilOMJ3q45iQgdC20nwJF1YFCi",
                "2237183"
            ),
            secondBlock
        )
        val thirdBlock = blockProducing.produceBlock(secondBlock)
        assertEquals(
            Block(
                "2",
                "-16346848297816610177277472819678465847532546009992945195534181085709118000000",
                "11456792849688944863687221543830969588625239580559150127325152649766174000000",
                "gSR62l1Z1yp203HXdqCg7sii4MFIHHkYRp3GntVLNkFo6xllu6Uyk44cqfSm1Nl9MunQnzRjl51RkQk5uxWMjtqXRaBCqMZndD4p9mz9DSCZUX0l7GtBb26iHAYsdfTb8mE5hjQ3XayUML8SWQ3i1O0iuYzTXnNUXDWg8BexQHbTM6NCZzQHeHK9pEsg8lm3d9gbWzb5tkwogtATRwRSSj4Srdfhkn8Cz4ol17inmnOpCKf1m8chu6ygVZXKCjHV",
                "1337748"
            ),
            thirdBlock
        )
        val forthBlock = blockProducing.produceBlock(thirdBlock)
        assertEquals(
            Block(
                "3",
                "11456792849688944863687221543830969588625239580559150127325152649766174000000",
                "52429649155304086803302802350634514915839687681695604606666469408555863000000",
                "MCJoNG6hj90q3uCtqkKAW91UNAH0wPXu9b3862NQGQxImsbABZMSIheripaY2gwmNaG17Fx20zX6VDIFRy5GeIrMih00pZjvpraOF5HvKUSaHkEFTeZlBc4B30ZmPrgDYxgI5EO2FpIVtVtuOsR4sCiSH53oVHa4PoBFvhg83OTP2MXoiSCu8YLD2n5lfHD7ISB5NIMdL6JpIzgw4IAT04eEfNE4p0ltdO2PIKKT3eHaHT1f1wsPaklAF5t7pdd2",
                "1349520"
            ),
            forthBlock
        )
    }

    @Test
    fun checkIncorrectCmdArguments() {
        var thrown = assertThrows<IllegalArgumentException> {
            cmdArgsParser(
                arrayOf(
                    "-p",
                    "8080",
                    "-n",
                    "httppp://127.0.0.1:8081,http://127.0.0.1:8082",
                    "-f"
                )
            )
        }
        assertEquals("Incorrect node address!", thrown.message)
        thrown = assertThrows {
            cmdArgsParser(
                arrayOf(
                    "-p",
                    "80a2",
                    "-n",
                    "httppp://127.0.0.1:8081,http://127.0.0.1:8082",
                    "-f"
                )
            )
        }
        assertEquals("Port should be an Int number!", thrown.message)
        thrown = assertThrows {
            cmdArgsParser(
                arrayOf(
                    "-n",
                    "http://127.0.0.1:8081,http://127.0.0.1:8082",
                    "-f"
                )
            )
        }
        assertEquals("Please, specify the server port!", thrown.message)
        thrown = assertThrows {
            cmdArgsParser(
                arrayOf(
                    "-p",
                    "8080",
                    "-f"
                )
            )
        }
        assertEquals("Please, specify the nodes!", thrown.message)
        thrown = assertThrows {
            cmdArgsParser(
                arrayOf(
                    "-p",
                    "8080",
                    "-n",
                    "httppp://127.0.0.1:8081http://127.0.0.1:8082",
                    "-f"
                )
            )
        }
        assertEquals("Incorrect node address!", thrown.message)
        val unrecognizedOption = assertThrows<UnrecognizedOptionException> {
            cmdArgsParser(
                arrayOf(
                    "-p",
                    "8080",
                    "-n",
                    "http://127.0.0.1:8081http://127.0.0.1:8082",
                    "-f",
                    "-r"
                )
            )
        }
        assertEquals("Unrecognized option: -r", unrecognizedOption.message)
        thrown = assertThrows {
            cmdArgsParser(
                arrayOf()
            )
        }
        assertEquals("Please, specify the server port!", thrown.message)
    }
}
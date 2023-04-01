import React from 'react';
import ReactDOM from 'react-dom';
import {Box, Button, Container, Divider, Paper, TextField, Typography} from "@mui/material";

const Login = () => {
    return <Container>
        <Paper elevation={5}>
            <Typography variant="h1">Login</Typography>
            <form action="/login" method="post">
                <Box>
                    <div>
                        <TextField
                            name="username"
                            id="username"
                            label="Username"
                            type="text"/>

                    </div>
                    <div>
                        <TextField
                            name="password"
                            id="password"
                            label="password"
                            type="password"/>

                    </div>

                    <Divider/>

                    <Button variant="contained" type="submit"> Login </Button>
                </Box>
            </form>
        </Paper>
    </Container>
}

ReactDOM.render(<Login/>, document.getElementById('app'));
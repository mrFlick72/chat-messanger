import React from 'react';
import ReactDOM from 'react-dom';
import {Box, Button, Divider, TextField} from "@mui/material";

const Login = () => {
    return <Box>
        <form action="/login" method="post">
            <Box>
                <TextField
                    name="username"
                    id="username"
                    label="Username"
                    type="text"/>

                <TextField
                    name="password"
                    id="password"
                    label="password"
                    type="password"/>

                <Divider/>

                <Button type="submit"> Login </Button>
            </Box>
        </form>
    </Box>
}

ReactDOM.render(<Login/>, document.getElementById('app'));